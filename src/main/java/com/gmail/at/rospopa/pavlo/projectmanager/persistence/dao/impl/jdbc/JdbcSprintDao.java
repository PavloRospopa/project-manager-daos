package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.SprintDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.SprintMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcSprintDao extends AbstractJdbcDao implements SprintDao {
    private static final String FIND_ALL_SQL = "SELECT id, name, startDate, completionDate, expectedCompletionDate, " +
            "project_id, previousSprint_id FROM sprints_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, startDate, completionDate, expectedCompletionDate, " +
            "project_id, previousSprint_id FROM sprints_view WHERE id=?";
    private static final String FIND_BY_PROJECT_SQL = "SELECT id, name, startDate, completionDate, expectedCompletionDate, " +
            "project_id, previousSprint_id FROM sprints_view WHERE project_id=?";
    private static final String FIND_BY_TASK_ID_SQL = "SELECT s.id, s.name, s.startDate, s.completionDate, " +
            "s.expectedCompletionDate, s.project_id, s.previousSprint_id FROM sprints_view s " +
            "JOIN tasks_view t ON s.id = t.sprint_id AND t.id =?";
    private static final String FIND_ACTIVE_BY_PR_SQL = "SELECT id, name, startDate, completionDate, " +
            "expectedCompletionDate, project_id, previousSprint_id FROM sprints_view " +
            "WHERE project_id =? AND CURRENT_DATE >= startDate AND completionDate IS NULL";
    private static final String FIND_COMPLETED_BY_PR_SQL = "SELECT id, name, startDate, completionDate, " +
            "expectedCompletionDate, project_id, previousSprint_id FROM sprints_view " +
            "WHERE project_id =? AND completionDate IS NOT NULL";

    private static final String OBJECT_TYPE = "object_types.sprint";
    private static final String NAME = "attributes.name";
    private static final String START_DATE = "attributes.startDate";
    private static final String COMPLETION_DATE = "attributes.completionDate";
    private static final String EX_COMPLETION_DATE = "attributes.expectedCompletionDate";
    private static final String PROJECT = "attributes.project";
    private static final String PREVIOUS_SPRINT = "attributes.previousSprint";

    public JdbcSprintDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<Sprint> findAll() {
        return jdbcTemplate.executeQuery(new SprintMapper(), FIND_ALL_SQL);
    }

    @Override
    public Sprint findById(Long id) {
        return jdbcTemplate.executeQuery(new SprintMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(Sprint entity) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getName(), entity.getId(),
                METAMODEL_PROP.getProperty(NAME));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, entity.getStartDate(), entity.getId(),
                METAMODEL_PROP.getProperty(START_DATE));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, entity.getCompletionDate(), entity.getId(),
                METAMODEL_PROP.getProperty(COMPLETION_DATE));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, entity.getExpectedCompletionDate(), entity.getId(),
                METAMODEL_PROP.getProperty(EX_COMPLETION_DATE));
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, entity.getProject().getId(), entity.getId(),
                METAMODEL_PROP.getProperty(PROJECT));

        Long previousSprintId = entity.getPreviousSprint() != null ? entity.getPreviousSprint().getId() : null;
        updatePreviousSprint(previousSprintId, entity.getId());
    }

    @Override
    public Long add(Sprint entity) {
        Long objectTypeId = Long.valueOf(METAMODEL_PROP.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(NAME), entity.getName());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                METAMODEL_PROP.getProperty(START_DATE), entity.getStartDate());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                METAMODEL_PROP.getProperty(COMPLETION_DATE), entity.getCompletionDate());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                METAMODEL_PROP.getProperty(EX_COMPLETION_DATE), entity.getExpectedCompletionDate());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                METAMODEL_PROP.getProperty(PROJECT), entity.getProject().getId());

        Sprint previousSprint = entity.getPreviousSprint();
        if (previousSprint != null) {
            jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                    METAMODEL_PROP.getProperty(PREVIOUS_SPRINT), previousSprint.getId());
        }

        return id;
    }

    @Override
    public List<Sprint> findByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new SprintMapper(), FIND_BY_PROJECT_SQL, id);
    }

    @Override
    public Sprint findByTaskId(Long id) {
        return jdbcTemplate.executeQuery(new SprintMapper(), FIND_BY_TASK_ID_SQL, id).get(0);
    }

    @Override
    public Sprint findActiveSprintByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new SprintMapper(), FIND_ACTIVE_BY_PR_SQL, id).get(0);
    }

    @Override
    public List<Sprint> findCompletedSprintsByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new SprintMapper(), FIND_COMPLETED_BY_PR_SQL, id);
    }

    private void updatePreviousSprint(Long previousSprintId, Long sprintId) {
        if (previousSprintId == null) {
            jdbcTemplate.executeUpdate(DELETE_REF_SQL, sprintId, METAMODEL_PROP.getProperty(PREVIOUS_SPRINT));
            return;
        }

        int isUpdated = jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, previousSprintId, sprintId,
                METAMODEL_PROP.getProperty(PREVIOUS_SPRINT));
        if (isUpdated == 0) {
            jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, sprintId,
                    METAMODEL_PROP.getProperty(PREVIOUS_SPRINT), previousSprintId);
        }
    }
}
