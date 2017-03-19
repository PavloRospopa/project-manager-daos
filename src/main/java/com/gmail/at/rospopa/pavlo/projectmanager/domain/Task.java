package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import java.util.List;
import java.util.Objects;

public class Task extends Entity {
    private int estimatedTime;
    private int spentTime;
    private Task parent;
    private Sprint sprint;
    private Employee.Position requiredEmpPosition;
    private String name;
    private String description;
    private List<Employee> employees;
    private Status status;

    public enum Status {
        UNASSIGNED, ACTIVE, COMPLETED
    }

    public Task() {
    }

    public Task(Long id) {
        super(id);
    }

    public Task(Long id, int estimatedTime, int spentTime, Task parent, Sprint sprint,
                Employee.Position requiredEmpPosition, String name, String description, Status status) {
        super(id);
        this.estimatedTime = estimatedTime;
        this.spentTime = spentTime;
        this.parent = parent;
        this.sprint = sprint;
        this.requiredEmpPosition = requiredEmpPosition;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(int spentTime) {
        this.spentTime = spentTime;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Employee.Position getRequiredEmpPosition() {
        return requiredEmpPosition;
    }

    public void setRequiredEmpPosition(Employee.Position requiredEmpPosition) {
        this.requiredEmpPosition = requiredEmpPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;
        if (getId() != null ? !getId().equals(task.getId()) : task.getId() != null) return false;
        if (estimatedTime != task.estimatedTime) return false;
        if (spentTime != task.spentTime) return false;
        if (parent != null ? (parent.getId() != null ? !parent.getId().equals(task.parent.getId())
                : task.parent.getId() != null) : task.parent != null) return false;
        if (sprint != null ? (sprint.getId() != null ? !sprint.getId().equals(task.sprint.getId())
                : task.sprint.getId() != null) : task.sprint != null) return false;
        if (requiredEmpPosition != task.requiredEmpPosition) return false;
        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        if (description != null ? !description.equals(task.description) : task.description != null) return false;
        return status == task.status;
    }

    @Override
    public int hashCode() {
        int result = estimatedTime;
        result = 31 * result + spentTime;
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (parent != null ? (parent.getId() != null ? parent.getId().hashCode() : 0) : 0);
        result = 31 * result + (sprint != null ? (sprint.getId() != null ? sprint.getId().hashCode() : 0) : 0);
        result = 31 * result + (requiredEmpPosition != null ? requiredEmpPosition.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
