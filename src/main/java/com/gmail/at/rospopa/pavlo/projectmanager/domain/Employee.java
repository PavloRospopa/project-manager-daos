package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

import java.util.List;

public class Employee extends User {
    private Position position;
    private List<Task> tasks;

    public Employee() {
    }

    public Employee(Long id) {
        super(id);
    }

    public Employee(Long id, String name, String surname, String username, String password, String email,
                    Role role, Position position) {
        super(id, name, surname, username, password, email, role);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", position=" + position +
                '}';
    }

    public enum Position {
        JUNIOR, MIDDLE, SENIOR
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        if (!super.equals(o)) return false;

        Employee employee = (Employee) o;

        return position == employee.position;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @Override
    public Prototype clone() {
        return new Employee(getId(), getName(), getSurname(), getUsername(), getPassword(),
                getEmail(), getRole(), getPosition());
    }
}
