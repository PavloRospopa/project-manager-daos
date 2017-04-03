package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.collections;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.InvalidObjectTypeException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractDatabase;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractTable;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionsDatabase extends AbstractDatabase {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void initDatabase() {
        tables = new HashMap<>();
        databaseInitialized = true;
    }

    @Override
    public void createTable(String tableName, Class<?> objectsType) {
        checkInitialization();
        checkTableAbsence(tableName);

        if (!Prototype.class.isAssignableFrom(objectsType)) {
            LOGGER.error("Trying to create table for storing objects of invalid type" +
                    " (does not extend Prototype class)");
            throw new InvalidObjectTypeException();
        }

        tables.put(tableName, new CollectionsTable<>(objectsType));
    }

    @Override
    public void dropTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        tables.remove(tableName);
    }

    @Override
    public <T> Map<Long, T> selectFrom(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<? extends Prototype> table = getTable(tableName);

        return table.selectAll().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (T) entry.getValue().clone()));
    }

    @Override
    public <T> T selectFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        AbstractTable<? extends Prototype> table = getTable(tableName);
        Prototype object = table.selectByKey(id);
        if (object != null) {
            return (T) object.clone();
        }
        return null;
    }

    @Override
    public <T> Map<Long, T> selectFrom(String tableName, Predicate<T> filter) {
        checkInitialization();
        checkTablePresence(tableName);

        Predicate<? extends Prototype> castedFilter = (Predicate<? extends Prototype>) filter;
        return (Map<Long, T>) selectFromHelper(tableName, castedFilter);
    }

    private <K extends Prototype> Map<Long, K> selectFromHelper(String tableName, Predicate<K> filter) {
        AbstractTable<K> table = getTable(tableName);
        return table.select(filter).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (K) entry.getValue().clone()));
    }

    @Override
    public <T> Long add(String tableName, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        Prototype o = (Prototype) object;

        AbstractTable<T> table = getTable(tableName);
        Long id = table.getAndGenerateNextId();
        T clone = (T) o.clone();
        table.put(id, clone);

        return id;
    }

    @Override
    public <T> boolean update(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        Prototype o = (Prototype) object;

        AbstractTable<T> table = getTable(tableName);
        T clone = (T) o.clone();

        return table.replace(id, clone);
    }

    @Override
    public boolean deleteFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        return tables.get(tableName).remove(id);
    }

    @Override
    protected <T> void insert(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        Prototype o = (Prototype) object;

        AbstractTable<T> table = getTable(tableName);
        T clone = (T) o.clone();
        table.put(id, clone);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}