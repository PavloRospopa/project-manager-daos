package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskTimeRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskTimeRequestMapper implements Mapper<TaskTimeRequest> {
    @Override
    public TaskTimeRequest map(ResultSet rs) throws SQLException {
        TaskTimeRequest request = new TaskTimeRequest();

        request.setId(rs.getLong("id"));
        request.setNewEstimatedTime(rs.getInt("newEstimatedTime"));
        request.setStatus(TaskTimeRequest.Status.valueOf(rs.getString("status")));
        request.setTask(new Task(rs.getLong("task_id")));
        request.setEmployee(new Employee(rs.getLong("employee_id")));

        return request;
    }
}
