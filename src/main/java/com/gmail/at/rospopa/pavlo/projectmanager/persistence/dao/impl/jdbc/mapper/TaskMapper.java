package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements Mapper<Task> {
    @Override
    public Task map(ResultSet rs) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setEstimatedTime(rs.getInt("estimatedTime"));
        task.setSpentTime(rs.getInt("spentTime"));
        task.setRequiredEmpPosition(Employee.Position.valueOf(rs.getString("requiredEmpPosition")));
        task.setDescription(rs.getString("description"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        Long parentTaskId = rs.getLong("parentTask_id");
        if (!rs.wasNull()) {
            task.setParent(new Task(parentTaskId));
        }
        task.setSprint(new Sprint(rs.getLong("sprint_id")));

        return task;
    }
}
