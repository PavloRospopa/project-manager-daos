package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.DatabaseNotInitializedException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.NoSuchTableException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.TableAdditionException;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDatabase implements Database{
    protected Map<String, AbstractTable<?>> tables;
    protected boolean databaseInitialized;

    protected abstract <T> void insert(String tableName, Long id, T object);
    protected abstract Logger getLogger();

    @Override
    public boolean isInitialized() {
        return databaseInitialized;
    }

    @Override
    public Set<String> getTableNames() {
        checkInitialization();
        return tables.keySet().stream().collect(Collectors.toSet());
    }

    @Override
    public void clearTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);
        tables.get(tableName).clear();
    }

    @Override
    public boolean tableExists(String tableName) {
        checkInitialization();
        return tables.containsKey(tableName);
    }

    @Override
    public Long getNextId(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);
        return tables.get(tableName).getNextId();
    }

    protected <T> AbstractTable<T> getTable(String tableName) {
        return (AbstractTable<T>) tables.get(tableName);
    }

    protected void checkInitialization() {
        if (!databaseInitialized) {
            getLogger().error("Database has to be initialized before working with it`s data");
            throw new DatabaseNotInitializedException();
        }
    }

    protected void checkTablePresence(String tableName) {
        if (!tables.containsKey(tableName)) {
            getLogger().error("Table with given name does not exist");
            throw new NoSuchTableException();
        }
    }

    protected void checkTableAbsence(String tableName) {
        if (tables.containsKey(tableName)) {
            getLogger().error("Table with given name already exists");
            throw new TableAdditionException();
        }
    }
}
