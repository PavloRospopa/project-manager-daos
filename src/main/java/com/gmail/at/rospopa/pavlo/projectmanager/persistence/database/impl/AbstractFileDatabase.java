package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.InvalidDatabaseStructureException;
import com.gmail.at.rospopa.pavlo.projectmanager.util.FileUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractFileDatabase extends AbstractDatabase {
    protected Path rootDir;
    protected Path metadataFile;
    protected Path tablesDir;
    protected boolean rewriteOldData;

    protected abstract void createTablesFromMetadataFile();

    @Override
    public void initDatabase() {
        tables = new HashMap<>();

        if (!rewriteOldData) {
            checkDatabaseDirectoryStructure();
            createTablesFromMetadataFile();
        }
        else {
            cleanDatabaseDirectories();
            createDatabaseStructure();
        }
        databaseInitialized = true;
    }

    @Override
    public <T> Map<Long, T> selectFrom(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<T> table = getTable(tableName);

        return table.selectAll();
    }

    @Override
    public <T> T selectFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<T> table = getTable(tableName);
        return table.selectByKey(id);
    }

    @Override
    public <T> Map<Long, T> selectFrom(String tableName, Predicate<T> filter) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<T> table = getTable(tableName);
        return table.select(filter);
    }

    @Override
    public <T> Long add(String tableName, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<T> table = getTable(tableName);
        Long id = table.getAndGenerateNextId();
        table.put(id, object);

        return id;
    }

    @Override
    public <T> void insert(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<T> table = getTable(tableName);
        table.put(id, object);
    }

    @Override
    public <T> boolean update(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<T> table = getTable(tableName);

        return table.replace(id, object);
    }

    @Override
    public boolean deleteFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        return tables.get(tableName).remove(id);
    }

    protected void deleteTableDir(String tableName) {
        Path tableDirPath = tablesDir.resolve(tableName);
        try {
            FileUtils.deleteFileTree(tableDirPath);
        } catch (IOException e) {
            getLogger().error("IO error occurred while trying to delete file tree of table directory " + tableName, e);
            throw new UncheckedIOException(e);
        }
    }

    protected void cleanDatabaseDirectories() {
        if (Files.exists(rootDir)) {
            try {
                FileUtils.deleteFileTree(rootDir);
            } catch (IOException e) {
                getLogger().error("IO error occurred while trying to delete database root directory recursively", e);
                throw new UncheckedIOException(e);
            }
        }
    }

    protected void createDatabaseStructure() {
        try {
            Files.createDirectories(tablesDir);
            Files.createFile(metadataFile);
        } catch (IOException e) {
            getLogger().error("Cannot create database directory structure in file system", e);
            throw new UncheckedIOException(e);
        }
    }

    protected void checkDatabaseDirectoryStructure() {
        if (!(Files.exists(rootDir) && Files.exists(tablesDir)) && Files.exists(metadataFile)) {
            getLogger().error("Database directory structure is invalid");
            throw new InvalidDatabaseStructureException();
        }
    }
}
