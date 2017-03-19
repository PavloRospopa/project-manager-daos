package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.ProjectMapper;


import java.sql.Connection;
import java.util.List;

public class JdbcProjectDao extends AbstractJdbcDao implements ProjectDao {

    private static final String FIND_ALL_SQL = "SELECT id, name, startDate, completionDate, expectedCompletionDate, " +
            "customer_id, project_manager_id FROM projects_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, startDate, completionDate, expectedCompletionDate, " +
            "customer_id, project_manager_id FROM projects_view WHERE id=?";
    private static final String FIND_BY_CUSTOMER_ID_SQL = "SELECT id, name, startDate, completionDate, " +
            "expectedCompletionDate, customer_id, project_manager_id FROM projects_view WHERE customer_id=?";
    private static final String FIND_BY_PR_MANAGER_ID_SQL = "SELECT id, name, startDate, completionDate, " +
            "expectedCompletionDate, customer_id, project_manager_id FROM projects_view WHERE project_manager_id=?";
    private static final String FIND_BY_SPRINT_ID_SQL = "SELECT id, name, startDate, completionDate, " +
            "expectedCompletionDate, customer_id, project_manager_id FROM projects_view " +
            "WHERE id=(SELECT project_id FROM sprints_view WHERE id=?)";
    private static final String FIND_ACTIVE_PROJECTS_SQL = "SELECT id, name, startDate, completionDate, " +
            "expectedCompletionDate, customer_id, project_manager_id FROM projects_view WHERE completionDate IS NULL";

    private static final String OBJECT_TYPE = "object_types.project";
    private static final String NAME = "attributes.name";
    private static final String START_DATE = "attributes.startDate";
    private static final String COMPLETION_DATE = "attributes.completionDate";
    private static final String EX_COMPLETION_DATE = "attributes.expectedCompletionDate";
    private static final String CUSTOMER = "attributes.customer";
    private static final String PROJECT_MANAGER = "attributes.projectManager";

    public JdbcProjectDao(Connection connection) {
        jdbcTemplate = new JdbcTemplate(connection);
    }

    @Override
    public List<Project> findAll() {
        return jdbcTemplate.executeQuery(new ProjectMapper(), FIND_ALL_SQL);
    }

    @Override
    public Project findById(Long id) {
        return jdbcTemplate.executeQuery(new ProjectMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(Project entity) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getName(), entity.getId(),
                metamodelProp.getProperty(NAME));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, entity.getStartDate(), entity.getId(),
                metamodelProp.getProperty(START_DATE));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, entity.getCompletionDate(), entity.getId(),
                metamodelProp.getProperty(COMPLETION_DATE));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, entity.getExpectedCompletionDate(), entity.getId(),
                metamodelProp.getProperty(EX_COMPLETION_DATE));
        updateCustomer(entity.getCustomer().getId(), entity.getId());
        updateProjectManager(entity.getProjectManager().getId(), entity.getId());
    }

    @Override
    public Long add(Project entity) {
        Long objectTypeId = Long.valueOf(metamodelProp.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                metamodelProp.getProperty(NAME), entity.getName());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                metamodelProp.getProperty(START_DATE), entity.getStartDate());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                metamodelProp.getProperty(COMPLETION_DATE), entity.getCompletionDate());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                metamodelProp.getProperty(EX_COMPLETION_DATE), entity.getExpectedCompletionDate());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                metamodelProp.getProperty(CUSTOMER), entity.getCustomer().getId());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                metamodelProp.getProperty(PROJECT_MANAGER), entity.getProjectManager().getId());

        return id;
    }

    @Override
    public List<Project> findByCustomerId(Long id) {
        return jdbcTemplate.executeQuery(new ProjectMapper(), FIND_BY_CUSTOMER_ID_SQL, id);
    }

    @Override
    public List<Project> findByProjectManagerId(Long id) {
        return jdbcTemplate.executeQuery(new ProjectMapper(), FIND_BY_PR_MANAGER_ID_SQL, id);
    }

    @Override
    public Project findBySprintId(Long id) {
        return jdbcTemplate.executeQuery(new ProjectMapper(), FIND_BY_SPRINT_ID_SQL, id).get(0);
    }

    @Override
    public List<Project> findActiveProjects() {
        return jdbcTemplate.executeQuery(new ProjectMapper(), FIND_ACTIVE_PROJECTS_SQL);
    }

    @Override
    public void updateProjectManager(Long projectManagerId, Long projectId) {
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, projectManagerId, projectId,
                metamodelProp.getProperty(PROJECT_MANAGER));
    }

    @Override
    public void updateCustomer(Long customerId, Long projectId) {
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, customerId, projectId,
                metamodelProp.getProperty(CUSTOMER));
    }
}
