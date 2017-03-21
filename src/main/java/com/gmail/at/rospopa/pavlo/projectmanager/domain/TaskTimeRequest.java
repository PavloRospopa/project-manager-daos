package com.gmail.at.rospopa.pavlo.projectmanager.domain;

public class TaskTimeRequest extends Entity {
    private Task task;
    private Employee employee;
    private int newEstimatedTime;
    private Status status;

    public enum Status {
        UNCONSIDERED, APPROVED, REFUSED
    }

    public TaskTimeRequest() {
    }

    public TaskTimeRequest(Long id) {
        super(id);
    }

    public TaskTimeRequest(Long id, Task task, Employee employee, int newEstimatedTime, Status status) {
        super(id);
        this.task = task;
        this.employee = employee;
        this.newEstimatedTime = newEstimatedTime;
        this.status = status;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNewEstimatedTime() {
        return newEstimatedTime;
    }

    public void setNewEstimatedTime(int newEstimatedTime) {
        this.newEstimatedTime = newEstimatedTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskTimeRequest)) return false;

        TaskTimeRequest that = (TaskTimeRequest) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (newEstimatedTime != that.newEstimatedTime) return false;
        if (task != null ? (task.getId() != null ? !task.getId().equals(that.task.getId())
                : that.task.getId() != null) : that.task != null) return false;
        if (employee != null ? (employee.getId() != null ? !employee.getId().equals(that.employee.getId())
                : that.employee.getId() != null) : that.employee != null) return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (task != null ? (task.getId() != null ? task.getId().hashCode() : 0) : 0);
        result = 31 * result + (employee != null ? (employee.getId() != null ? employee.getId().hashCode() : 0) : 0);
        result = 31 * result + newEstimatedTime;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
