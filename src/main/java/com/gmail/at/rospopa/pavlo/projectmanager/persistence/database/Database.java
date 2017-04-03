package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface Database {

    void initDatabase();
    boolean isInitialized();

    Set<String> getTableNames();
    void createTable(String tableName, Class<?> objectsType);
    void dropTable(String tableName);
    void clearTable(String tableName);
    boolean tableExists(String tableName);
    Long getNextId(String tableName);

    <T> Map<Long, T> selectFrom(String tableName);
    <T> T selectFrom(String tableName, Long id);
    <T> Map<Long, T> selectFrom(String tableName, Predicate<T> filter);

    <T> Long add(String tableName, T object);
    <T> boolean update(String tableName, Long id, T object);
    boolean deleteFrom(String tableName, Long id);
}
