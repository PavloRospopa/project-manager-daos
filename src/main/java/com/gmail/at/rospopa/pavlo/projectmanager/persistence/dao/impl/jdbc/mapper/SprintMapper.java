package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SprintMapper implements Mapper<Sprint> {

    @Override
    public Sprint map(ResultSet rs) throws SQLException {
        Sprint sprint = new Sprint();

        sprint.setId(rs.getLong("id"));
        sprint.setName(rs.getString("name"));
        sprint.setStartDate(rs.getDate("startDate"));
        sprint.setCompletionDate(rs.getDate("completionDate"));
        sprint.setExpectedCompletionDate(rs.getDate("expectedCompletionDate"));
        sprint.setProject(new Project(rs.getLong("project_id")));
        Long previousSprintId = rs.getLong("previousSprint_id");
        if (!rs.wasNull()) {
            sprint.setPreviousSprint(new Sprint(previousSprintId));
        }

        return sprint;
    }
}
