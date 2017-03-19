package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Entity;

import java.util.List;

public interface Dao<PK, T extends Entity> {

    List<T> findAll();
    T findById(PK id);
    void delete(PK id);
    void update(T entity);
    PK add(T entity);
}
