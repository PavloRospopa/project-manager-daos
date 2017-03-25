package com.gmail.at.rospopa.pavlo.projectmanager.util;

public class PrototypePair<L extends Prototype, R extends Prototype> extends Pair<L, R> implements Prototype {
    public PrototypePair(L left, R right) {
        super(left, right);
    }

    @Override
    public Prototype clone() {
        return new PrototypePair<>((L) getLeft().clone(), (R) getRight().clone());
    }
}
