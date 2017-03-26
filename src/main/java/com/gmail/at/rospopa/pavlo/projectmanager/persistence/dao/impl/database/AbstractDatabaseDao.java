package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class AbstractDatabaseDao {

    protected Database database;

    public AbstractDatabaseDao(Database database) {
        this.database = database;
    }

    protected <T extends Prototype> List<T> selectFrom(String tableName, Predicate<T> filter) {
        Map<Long, T> entityMap = database.selectFrom(tableName, filter);

        return entityMap.values().stream().collect(Collectors.toList());
    }

    protected <T extends Prototype> List<T> selectFrom(String tableName) {
        Map<Long, T> entityMap = database.selectFrom(tableName);

        return entityMap.values().stream().collect(Collectors.toList());
    }
}
