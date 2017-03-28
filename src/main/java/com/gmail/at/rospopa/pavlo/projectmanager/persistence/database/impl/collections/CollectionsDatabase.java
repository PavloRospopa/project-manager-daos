package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.collections;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.*;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionsDatabase implements Database {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, CollectionsTable<?>> tables;

    private boolean databaseInitialized;

    @Override
    public void initDatabase() {
        tables = new HashMap<>();
        databaseInitialized = true;
    }

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
    public void createTable(String tableName, Class<? extends Prototype> objectsType) {
        checkInitialization();
        checkTableAbsence(tableName);

        tables.put(tableName, new CollectionsTable<>(objectsType));
    }

    @Override
    public void dropTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        tables.remove(tableName);
    }

    @Override
    public void clearTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<?> table = tables.get(tableName);
        table.clear();
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

        CollectionsTable<?> table = tables.get(tableName);

        return table.getNextId();
    }

    @Override
    public <T extends Prototype> Map<Long, T> selectFrom(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<T> table = getTable(tableName);

        return table.selectAll().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (T) entry.getValue().clone()));
    }

    @Override
    public <T extends Prototype> T selectFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<T> table = getTable(tableName);
        T object = table.selectByKey(id);
        if (object != null) {
            return (T) object.clone();
        }
        return null;
    }

    @Override
    public <T extends Prototype> Map<Long, T> selectFrom(String tableName, Predicate<T> filter) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<T> table = getTable(tableName);
        return table.select(filter).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (T) entry.getValue().clone()));
    }

    @Override
    public  <T extends Prototype> Long add(String tableName, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<T> table = getTable(tableName);
        Long id = table.getAndGenerateNextId();
        T clone = (T) object.clone();
        table.put(id, clone);

        return id;
    }

    @Override
    public <T extends Prototype> void insert(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<T> table = getTable(tableName);
        T clone = (T) object.clone();
        table.put(id, clone);
    }

    @Override
    public <T extends Prototype> boolean update(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<T> table = getTable(tableName);
        T clone = (T) object.clone();

        return table.replace(id, clone);
    }

    @Override
    public boolean deleteFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        CollectionsTable<?> table = tables.get(tableName);

        return table.remove(id);
    }

    private <T> CollectionsTable<T> getTable(String tableName) {
        return (CollectionsTable<T>) tables.get(tableName);
    }

    private void checkInitialization() {
        if (!databaseInitialized) {
            LOGGER.error("Database has to be initialized before working with it`s data");
            throw new DatabaseNotInitializedException();
        }
    }

    private void checkTablePresence(String tableName) {
        if (!tables.containsKey(tableName)) {
            LOGGER.error("Table with given name does not exist");
            throw new NoSuchTableException();
        }
    }

    private void checkTableAbsence(String tableName) {
        if (tables.containsKey(tableName)) {
            LOGGER.error("Table with given name already exists");
            throw new TableAdditionException();
        }
    }
}