package com.gmail.at.rospopa.pavlo.projectmanager.domain;

public class ProjectManager extends User {
    public ProjectManager() {
    }

    public ProjectManager(Long id) {
        super(id);
    }

    public ProjectManager(Long id, String name, String surname, String username, String password, String email, Role role) {
        super(id, name, surname, username, password, email, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectManager)) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
