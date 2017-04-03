package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.InvalidTableStructureException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.TableNotInitializedException;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractFileTable<T> extends AbstractTable<T> {
    protected Path tableDirPath;
    protected Path nextIdFilePath;
    protected Map<Integer, Path> tableFilesMap;
    protected boolean tableInitialized;

    protected abstract Logger getLogger();
    protected abstract Long getFirstId();
    protected abstract int getNumberOfFiles();
    protected abstract String getFileExtension();

    public void initTable(boolean isNew) {
        if (!isNew) {
            checkTableStructure();
        } else {
            createTableStructure();
        }
        nextId = readNextId();
        tableInitialized = true;
    }

    @Override
    public Long getAndGenerateNextId() {
        writeNextId(nextId + 1);
        return nextId++;
    }

    @Override
    public void clear() {
        checkInitialization();
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            try {
                Files.write(entry.getValue(), Collections.emptyList());
            } catch (IOException e) {
                getLogger().error("IO error while trying to clear data from table files", e);
                throw new UncheckedIOException(e);
            }
        }

        try {
            Files.write(nextIdFilePath, Collections.singleton(getFirstId().toString()));
        } catch (IOException e) {
            getLogger().error("IO error while trying to rewrite next id file", e);
            throw new UncheckedIOException(e);
        }
    }

    protected Path getTempFilePath(Path tableFilePath) {
        String tempFileName = String.format("_temp%s", tableFilePath.getFileName().toString());
        return tableDirPath.resolve(tempFileName);
    }

    protected void generateTableFiles(String tableName) {
        for (int i = 0; i < getNumberOfFiles(); i++) {
            String tableFileName = String.format("%s%d.%s", tableName, i, getFileExtension());
            Path tableFilePath = tableDirPath.resolve(tableFileName);
            tableFilesMap.put(i, tableFilePath);
        }
    }

    protected Path getTableFilePath(Long id) {
        int tableNumber = (int) id.longValue() % getNumberOfFiles();
        return tableFilesMap.get(tableNumber);
    }

    protected void checkInitialization() {
        if (!tableInitialized) {
            getLogger().error("Table has to be initialized before working with it`s data");
            throw new TableNotInitializedException();
        }
    }

    protected void createTempFile(Path tempFilePath) {
        try {
            Files.createFile(tempFilePath);
        } catch (IOException e) {
            getLogger().error("Cannot create temporary table file in file system", e);
            throw new UncheckedIOException(e);
        }
    }

    protected void deleteTableFile(Path tableFilePath) {
        try {
            Files.delete(tableFilePath);
        } catch (IOException e) {
            getLogger().error("IO error while trying to delete table file", e);
            throw new UncheckedIOException(e);
        }
    }

    protected void renameTempFile(Path tableFilePath, Path tempFilePath) {
        File tempFile = tempFilePath.toFile();
        if (!tempFile.renameTo(tableFilePath.toFile())) {
            getLogger().error("Cannot rename temporary table file");
            throw new RuntimeException();
        }
    }

    private Long readNextId() {
        String nextIdStr;
        try {
            nextIdStr = Files.readAllLines(nextIdFilePath).get(0);
        } catch (IOException e) {
            getLogger().error("Cannot read data from nextId file", e);
            throw new UncheckedIOException(e);
        }
        return Long.valueOf(nextIdStr);
    }

    private void writeNextId(Long nextId) {
        try {
            Files.write(nextIdFilePath, Collections.singleton(nextId.toString()));
        } catch (IOException e) {
            getLogger().error("Cannot write data to nextId file", e);
            throw new UncheckedIOException(e);
        }
    }

    private void createTableStructure() {
        try {
            Files.createDirectories(tableDirPath);
            for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
                Files.createFile(entry.getValue());
            }
            Files.createFile(nextIdFilePath);
            Files.write(nextIdFilePath, Collections.singleton(getFirstId().toString()));
        } catch (IOException e) {
            getLogger().error("Cannot create table structure in file system", e);
            throw new UncheckedIOException(e);
        }
    }

    private void checkTableStructure() {
        if (!Files.exists(nextIdFilePath)) {
            getLogger().error("Table structure is invalid");
            throw new InvalidTableStructureException();
        }
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            if (!Files.exists(entry.getValue())) {
                getLogger().error("Table structure is invalid");
                throw new InvalidTableStructureException();
            }
        }
    }
}
