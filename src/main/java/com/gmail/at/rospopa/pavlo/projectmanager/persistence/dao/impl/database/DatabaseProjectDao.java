package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.*;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.sql.Date;
import java.util.List;

public class DatabaseProjectDao extends AbstractDatabaseDao implements ProjectDao {
    private static final String PROJECTS_TABLE = "PROJECTS";
    private static final String SPRINTS_TABLE = "SPRINTS";

    public DatabaseProjectDao(Database database) {
        super(database);
    }

    @Override
    public List<Project> findAll() {
        return selectFrom(PROJECTS_TABLE);
    }

    @Override
    public Project findById(Long id) {
        return database.selectFrom(PROJECTS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(PROJECTS_TABLE, id);
    }

    @Override
    public void update(Project entity) {
        database.update(PROJECTS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(Project entity) {
        Long id = database.getNextId(PROJECTS_TABLE);
        entity.setId(id);

        return database.add(PROJECTS_TABLE, entity);
    }

    @Override
    public List<Project> findByCustomerId(Long id) {
        return selectFrom(PROJECTS_TABLE,
                p -> p.getCustomer() != null && p.getCustomer().getId().equals(id));
    }

    @Override
    public List<Project> findByProjectManagerId(Long id) {
        return selectFrom(PROJECTS_TABLE,
                p -> p.getProjectManager() != null && p.getProjectManager().getId().equals(id));
    }

    @Override
    public Project findBySprintId(Long id) {
        Sprint sprint = database.selectFrom(SPRINTS_TABLE, id);
        if (sprint != null) {
            Long projectId = sprint.getProject().getId();

            return database.selectFrom(PROJECTS_TABLE, projectId);
        }
        return null;
    }

    @Override
    public List<Project> findActiveProjects() {
        Date currentDate = new Date(new java.util.Date().getTime());

        return selectFrom(PROJECTS_TABLE,
                p -> p.getStartDate() != null
                        && p.getStartDate().compareTo(currentDate) < 0
                        && p.getCompletionDate() == null);
    }

    @Override
    public void updateProjectManager(Long projectManagerId, Long projectId) {
        Project project = findById(projectId);
        project.setProjectManager(new ProjectManager(projectManagerId));

        database.update(PROJECTS_TABLE, projectId, project);
    }

    @Override
    public void updateCustomer(Long customerId, Long projectId) {
        Project project = findById(projectId);
        project.setCustomer(new Customer(customerId));

        database.update(PROJECTS_TABLE, projectId, project);
    }
}
