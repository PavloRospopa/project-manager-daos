package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.collections;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractTable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionsTable<T> extends AbstractTable<T> {
    private Map<Long, T> objectsMap;

    public CollectionsTable(Class<T> objectsType) {
        this.objectsType = objectsType;
        objectsMap = new HashMap<>();
        nextId = 1L;
    }

    @Override
    public Long getAndGenerateNextId() {
        return nextId++;
    }

    @Override
    public void put(Long key, T value) {
        objectsMap.put(key, value);
    }

    @Override
    public boolean remove(Long key) {
        if (objectsMap.containsKey(key)) {
            objectsMap.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(Long key, T value) {
        if (objectsMap.containsKey(key)) {
            objectsMap.put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public Map<Long, T> selectAll() {
        return Collections.unmodifiableMap(objectsMap);
    }

    @Override
    public T selectByKey(Long key) {
        return objectsMap.get(key);
    }

    @Override
    public Map<Long, T> select(Predicate<T> filter) {
        return objectsMap.entrySet().stream()
                .filter(entry -> filter.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void clear() {
        objectsMap.clear();
    }
}
