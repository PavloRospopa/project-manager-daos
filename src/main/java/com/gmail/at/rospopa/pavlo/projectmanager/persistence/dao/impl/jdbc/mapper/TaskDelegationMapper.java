package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskDelegation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskDelegationMapper implements Mapper<TaskDelegation> {
    @Override
    public TaskDelegation map(ResultSet rs) throws SQLException {
        TaskDelegation taskDelegation = new TaskDelegation();

        taskDelegation.setId(rs.getLong("id"));
        taskDelegation.setStartDateTime(rs.getTimestamp("startDateTime"));
        taskDelegation.setCompletionDateTime(rs.getTimestamp("completionDateTime"));
        taskDelegation.setStatus(TaskDelegation.Status.valueOf(rs.getString("status")));
        taskDelegation.setTask(new Task(rs.getLong("task_id")));
        taskDelegation.setEmployee(new Employee(rs.getLong("employee_id")));

        return taskDelegation;
    }
}
