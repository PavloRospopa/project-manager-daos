package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

abstract class AbstractDatabaseDao {

    protected Database database;

    public AbstractDatabaseDao(Database database) {
        this.database = database;
    }

    protected <T> List<T> selectFrom(String tableName, Predicate<T> filter) {
        Map<Long, T> entityMap = database.selectFrom(tableName, filter);

        return entityMap.values().stream().collect(Collectors.toList());
    }

    protected <T> List<T> selectFrom(String tableName) {
        Map<Long, T> entityMap = database.selectFrom(tableName);

        return entityMap.values().stream().collect(Collectors.toList());
    }
}
