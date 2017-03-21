package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectMapper implements Mapper<Project> {

    @Override
    public Project map(ResultSet rs) throws SQLException {
        Project project = new Project();

        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setStartDate(rs.getDate("startDate"));
        project.setCompletionDate(rs.getDate("completionDate"));
        project.setExpectedCompletionDate(rs.getDate("expectedCompletionDate"));
        Long customerId = rs.getLong("customer_id");
        if (!rs.wasNull()) {
            project.setCustomer(new Customer(customerId));
        }
        Long projectManagerId = rs.getLong("project_manager_id");
        if (!rs.wasNull()) {
            project.setProjectManager(new ProjectManager(projectManagerId));
        }

        return project;
    }
}
