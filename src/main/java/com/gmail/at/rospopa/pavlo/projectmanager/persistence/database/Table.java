package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database;

import java.util.Map;
import java.util.function.Predicate;

public interface Table<K, V> {

    V put(K key, V value);
    boolean remove(K key);
    boolean replace(K key, V value);
    Map<K, V> selectAll();
    V selectByKey(K key);
    Map<K, V> select(Predicate<V> filter);
    void clear();
    K getNextId();
    K getAndGenerateNextId();
}
