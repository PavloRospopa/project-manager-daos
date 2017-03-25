package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface Database {

    void initDatabase();
    boolean isInitialized();

    Set<String> getTableNames();
    void createTable(String tableName, Class<? extends Prototype> objectsType);
    void dropTable(String tableName);
    void clearTable(String tableName);
    boolean tableExists(String tableName);
    Long getNextId(String tableName);

    <T extends Prototype> Map<Long, T> selectFrom(String tableName);
    <T extends Prototype> T selectFrom(String tableName, Long id);
    <T extends Prototype> Map<Long, T> selectFrom(String tableName, Predicate<T> filter);

    //entity is copied. copy is stored in database
    <T extends Prototype> Long add(String tableName, T object);

    //add to database without generating id
    <T extends Prototype> void insert(String tableName, Long id, T object);

    <T extends Prototype> boolean update(String tableName, Long id, T object);

    boolean deleteFrom(String tableName, Long id);
}
