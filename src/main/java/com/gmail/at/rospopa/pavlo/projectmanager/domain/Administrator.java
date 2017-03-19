package com.gmail.at.rospopa.pavlo.projectmanager.domain;

public class Administrator extends User {
    public Administrator() {
    }

    public Administrator(Long id) {
        super(id);
    }

    public Administrator(Long id, String name, String surname, String username, String password, String email, Role role) {
        super(id, name, surname, username, password, email, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Administrator)) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
