package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectManagerDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.util.List;

public class DatabaseProjectManagerDao extends AbstractDatabaseDao implements ProjectManagerDao {
    private static final String PROJECT_MANAGERS_TABLE = "PROJECT_MANAGERS";
    private static final String PROJECTS_TABLE = "PROJECTS";

    public DatabaseProjectManagerDao(Database database) {
        super(database);
    }

    @Override
    public List<ProjectManager> findAll() {
        return selectFrom(PROJECT_MANAGERS_TABLE);
    }

    @Override
    public ProjectManager findById(Long id) {
        return database.selectFrom(PROJECT_MANAGERS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(PROJECT_MANAGERS_TABLE, id);
    }

    @Override
    public void update(ProjectManager entity) {
        database.update(PROJECT_MANAGERS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(ProjectManager entity) {
        Long id = database.getNextId(PROJECT_MANAGERS_TABLE);
        entity.setId(id);

        return database.add(PROJECT_MANAGERS_TABLE, entity);
    }

    @Override
    public ProjectManager findByProjectId(Long id) {
        Project project = database.selectFrom(PROJECTS_TABLE, id);
        if (project != null && project.getProjectManager() != null) {
            Long projectManagerId = project.getProjectManager().getId();

            return database.selectFrom(PROJECT_MANAGERS_TABLE, projectManagerId);
        }
        return null;
    }
}
