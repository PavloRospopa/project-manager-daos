package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectManagerMapper implements Mapper<ProjectManager> {
    @Override
    public ProjectManager map(ResultSet rs) throws SQLException {
        ProjectManager projectManager = new ProjectManager();

        projectManager.setId(rs.getLong("id"));
        projectManager.setName(rs.getString("name"));
        projectManager.setSurname(rs.getString("surname"));
        projectManager.setUsername(rs.getString("username"));
        projectManager.setPassword(rs.getString("password"));
        projectManager.setEmail(rs.getString("email"));
        projectManager.setRole(User.Role.PROJECT_MANAGER);

        return projectManager;
    }
}
