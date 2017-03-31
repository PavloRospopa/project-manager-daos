package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

public class DependenciesPair extends Pair<Long, Long> implements Prototype {
    public DependenciesPair(Long left, Long right) {
        super(left, right);
    }

    private DependenciesPair() {
        super(null, null);
    }

    @Override
    public Prototype clone() {
        return new DependenciesPair(getLeft(), getRight());
    }
}
