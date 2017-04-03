package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Table;

public abstract class AbstractTable<T> implements Table<Long, T> {
    protected Class<T> objectsType;
    protected Long nextId;

    @Override
    public Class<T> getObjectsType() {
        return objectsType;
    }

    @Override
    public Long getNextId() {
        return nextId;
    }
}
