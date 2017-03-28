package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.json;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Table;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.InvalidTableStructureException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.TableNotInitializedException;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

public class JsonTable<T> implements Table<Long, T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties JSON_DB_PROP = PropertiesLoader.getInstance().getJsonDBProperties();

    private static final String NEXT_ID_FILE_NAME = JSON_DB_PROP.getProperty("json.database.next_id.file");
    private static final String FIRST_ID = JSON_DB_PROP.getProperty("json.database.first_id");
    private static final int NUMBER_OF_FILES =
            Integer.parseInt(JSON_DB_PROP.getProperty("json.database.number_of_files"));

    private Type pairType;
    private Class<T> objectsType;
    private Gson gson;

    private Path tableDirPath;
    private Path nextIdFilePath;

    private Long nextId;
    private Map<Integer, Path> tableFilesMap;

    private boolean tableInitialized;

    public JsonTable(Class<T> objectsType, String tableName, Path tablesDir, Gson gson) {
        this.objectsType = objectsType;
        pairType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{Long.class, objectsType};
            }

            @Override
            public Type getRawType() {
                return Pair.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };

        this.tableDirPath = tablesDir.resolve(tableName);
        this.nextIdFilePath = tableDirPath.resolve(NEXT_ID_FILE_NAME);
        this.gson = gson;
        tableFilesMap = new HashMap<>();
        generateTableFiles(tableName);
    }

    public void initJsonTable(boolean isNew) {
        if (!isNew) {
            checkTableStructure();
        } else {
            createTableStructure();
        }
        nextId = readNextId();
        tableInitialized = true;
    }

    @Override
    public void put(Long key, T value) {
        checkInitialization();
        Pair<Long, T> pair = new Pair<>(key, value);
        String json = gson.toJson(pair, pairType);

        Path tableFilePath = getTableFilePath(key);
        try {
            Files.write(tableFilePath, Collections.singleton(json), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Cannot write new data to json db table file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean remove(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        try {
            Files.createFile(tempFilePath);
        } catch (IOException e) {
            LOGGER.error("Cannot create temporary table file in file system", e);
            throw new UncheckedIOException(e);
        }

        try (BufferedReader reader = Files.newBufferedReader(tableFilePath);
             BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = gson.fromJson(line, pairType);
                if (!pair.getLeft().equals(key)) {
                    writer.write(line);
                    writer.newLine();
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file or write to temp table file", e);
            throw new UncheckedIOException(e);
        }
        try {
            Files.delete(tableFilePath);
        } catch (IOException e) {
            LOGGER.error("IO error while trying to delete table file", e);
            throw new UncheckedIOException(e);
        }
        File tempFile = tempFilePath.toFile();
        if (!tempFile.renameTo(tableFilePath.toFile())) {
            LOGGER.error("Cannot rename temporary table file");
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    public boolean replace(Long key, T value) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        try {
            Files.createFile(tempFilePath);
        } catch (IOException e) {
            LOGGER.error("Cannot create temporary table file in file system", e);
            throw new UncheckedIOException(e);
        }
        try (BufferedReader reader = Files.newBufferedReader(tableFilePath);
             BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = gson.fromJson(line, pairType);
                if (pair.getLeft().equals(key)) {
                    Pair<Long, T> updatedPair = new Pair<>(pair.getLeft(), value);
                    String updatedPairJson = gson.toJson(updatedPair, pairType);
                    writer.write(updatedPairJson);
                } else {
                    writer.write(line);
                }
                writer.newLine();
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file or write to temp table file", e);
            throw new UncheckedIOException(e);
        }
        try {
            Files.delete(tableFilePath);
        } catch (IOException e) {
            LOGGER.error("IO error while trying to delete table file", e);
            throw new UncheckedIOException(e);
        }
        File tempFile = tempFilePath.toFile();
        if (!tempFile.renameTo(tableFilePath.toFile())) {
            LOGGER.error("Cannot rename temporary table file");
            throw new RuntimeException();
        }
        return true;
    }

    @Override
    public T selectByKey(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);

        try (BufferedReader reader = Files.newBufferedReader(tableFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = gson.fromJson(line, pairType);
                if (pair.getLeft().equals(key)) {
                    return pair.getRight();
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file " + tableFilePath.getFileName().toString(), e);
            throw new UncheckedIOException(e);
        }
        return null;
    }

    @Override
    public Map<Long, T> selectAll() {
        return select(o -> true);
    }

    @Override
    public Map<Long, T> select(Predicate<T> filter) {
        checkInitialization();
        Map<Long, T> objectsMap = new HashMap<>();
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            try (BufferedReader reader = Files.newBufferedReader(entry.getValue())) {
                String line = reader.readLine();
                while (line != null) {
                    Pair<Long, T> pair = gson.fromJson(line, pairType);
                    if (filter.test(pair.getRight())) {
                        objectsMap.put(pair.getLeft(), pair.getRight());
                    }
                    line = reader.readLine();
                }
            } catch (IOException e) {
                LOGGER.error("Cannot read from table file " + entry.getValue().getFileName().toString(), e);
                throw new UncheckedIOException(e);
            }
        }
        return objectsMap;
    }

    @Override
    public void clear() {
        checkInitialization();
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            try {
                Files.write(entry.getValue(), Collections.emptyList());
            } catch (IOException e) {
                LOGGER.error("IO error while trying to clear data from table files", e);
                throw new UncheckedIOException(e);
            }
        }

        try {
            Files.write(nextIdFilePath, Collections.singleton(FIRST_ID));
        } catch (IOException e) {
            LOGGER.error("IO error while trying to rewrite next id file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Long getNextId() {
        return nextId;
    }

    @Override
    public Long getAndGenerateNextId() {
        writeNextId(nextId + 1);
        return nextId++;
    }

    @Override
    public Class<T> getObjectsType() {
        return objectsType;
    }

    private Long readNextId() {
        String nextIdStr;
        try {
            nextIdStr = Files.readAllLines(nextIdFilePath).get(0);
        } catch (IOException e) {
            LOGGER.error("Cannot read data from nextId file", e);
            throw new UncheckedIOException(e);
        }
        return Long.valueOf(nextIdStr);
    }

    private void writeNextId(Long nextId) {
        try {
            Files.write(nextIdFilePath, Collections.singleton(nextId.toString()));
        } catch (IOException e) {
            LOGGER.error("Cannot write nextId to file", e);
            throw new UncheckedIOException(e);
        }
    }

    private Path getTempFilePath(Path tableFilePath) {
        String tempFileName = String.format("_temp%s", tableFilePath.getFileName().toString());
        return tableDirPath.resolve(tempFileName);
    }

    private void generateTableFiles(String tableName) {
        for (int i = 0; i < NUMBER_OF_FILES; i++) {
            String tableFileName = String.format("%s%d.jsondb", tableName, i);
            Path tableFilePath = tableDirPath.resolve(tableFileName);
            tableFilesMap.put(i, tableFilePath);
        }
    }

    private Path getTableFilePath(Long id) {
        int tableNumber = (int) id.longValue() % NUMBER_OF_FILES;
        return tableFilesMap.get(tableNumber);
    }

    private void createTableStructure() {
        try {
            Files.createDirectories(tableDirPath);
            for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
                Files.createFile(entry.getValue());
            }
            Files.createFile(nextIdFilePath);
            Files.write(nextIdFilePath, Collections.singleton(FIRST_ID));
        } catch (IOException e) {
            LOGGER.error("Cannot create table structure in file system", e);
            throw new UncheckedIOException(e);
        }
    }

    private void checkTableStructure() {
        if (!Files.exists(nextIdFilePath)) {
            LOGGER.error("Json table structure is invalid");
            throw new InvalidTableStructureException();
        }
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            if (!Files.exists(entry.getValue())) {
                LOGGER.error("Json table structure is invalid");
                throw new InvalidTableStructureException();
            }
        }
    }

    private void checkInitialization() {
        if (!tableInitialized) {
            LOGGER.error("Table has to be initialized before working with it`s data");
            throw new TableNotInitializedException();
        }
    }
}
