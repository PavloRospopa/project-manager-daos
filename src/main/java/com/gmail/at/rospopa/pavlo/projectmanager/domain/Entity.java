package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

public abstract class Entity implements Prototype {
    private Long id;

    @Override
    public abstract Prototype clone();

    public Entity() {
    }

    public Entity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
