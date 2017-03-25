package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

import java.sql.Timestamp;

public class TaskDelegation extends Entity{
    private Task task;
    private Employee employee;
    private Timestamp startDateTime;
    private Timestamp completionDateTime;
    private Status status;

    public enum Status {
        UNCONFIRMED, ACTIVE, COMPLETED
    }

    public TaskDelegation() {
    }

    public TaskDelegation(Long id) {
        super(id);
    }

    public TaskDelegation(Long id, Task task, Employee employee, Timestamp startDateTime,
                          Timestamp completionDateTime, Status status) {
        super(id);
        this.task = task;
        this.employee = employee;
        this.startDateTime = startDateTime;
        this.completionDateTime = completionDateTime;
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

    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Timestamp getCompletionDateTime() {
        return completionDateTime;
    }

    public void setCompletionDateTime(Timestamp completionDateTime) {
        this.completionDateTime = completionDateTime;
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
        if (!(o instanceof TaskDelegation)) return false;

        TaskDelegation that = (TaskDelegation) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (task != null ? (task.getId() != null ? !task.getId().equals(that.task.getId())
                : that.task.getId() != null) : that.task != null) return false;
        if (employee != null ? (employee.getId() != null ? !employee.getId().equals(that.employee.getId())
                : that.employee.getId() != null) : that.employee != null) return false;
        if (startDateTime != null ? !startDateTime.equals(that.startDateTime) : that.startDateTime != null)
            return false;
        if (completionDateTime != null ? !completionDateTime.equals(that.completionDateTime) : that.completionDateTime != null)
            return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (task != null ? (task.getId() != null ? task.getId().hashCode() : 0) : 0);
        result = 31 * result + (employee != null ? (employee.getId() != null ? employee.getId().hashCode() : 0) : 0);
        result = 31 * result + (startDateTime != null ? startDateTime.hashCode() : 0);
        result = 31 * result + (completionDateTime != null ? completionDateTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public Prototype clone() {
        Task task = getTask() != null ?
                new Task(getTask().getId()) : null;
        Employee employee = getEmployee() != null ?
                new Employee(getEmployee().getId()) : null;

        return new TaskDelegation(getId(), task, employee, getStartDateTime(), getCompletionDateTime(), getStatus());
    }
}
