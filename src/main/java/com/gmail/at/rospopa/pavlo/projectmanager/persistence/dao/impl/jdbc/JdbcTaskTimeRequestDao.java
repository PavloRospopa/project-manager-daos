package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskTimeRequest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.TaskTimeRequestDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.TaskTimeRequestMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcTaskTimeRequestDao extends AbstractJdbcDao implements TaskTimeRequestDao {
    private static final String FIND_ALL_SQL = "SELECT id, newEstimatedTime, status, task_id, employee_id " +
            "FROM taskTimeRequests_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, newEstimatedTime, status, task_id, employee_id " +
            "FROM taskTimeRequests_view WHERE id=?";
    private static final String FIND_BY_TASK_SQL = "SELECT id, newEstimatedTime, status, task_id, employee_id " +
            "FROM taskTimeRequests_view WHERE task_id=?";
    private static final String FIND_BY_EMP_SQL = "SELECT id, newEstimatedTime, status, task_id, employee_id " +
            "FROM taskTimeRequests_view WHERE employee_id=?";
    private static final String FIND_BY_PROJECT_SQL = "SELECT ttr.id, ttr.newEstimatedTime, ttr.status, ttr.task_id, " +
            "ttr.employee_id FROM taskTimeRequests_view ttr " +
            "JOIN tasks_view t ON ttr.task_id = t.id " +
            "JOIN sprints_view s ON t.sprint_id = s.id " +
            "JOIN projects_view p ON s.project_id = p.id " +
            "WHERE p.id =?";
    private static final String FIND_SOME_BY_PROJECT_SQL = "SELECT ttr.id, ttr.newEstimatedTime, ttr.status, ttr.task_id, " +
            "ttr.employee_id FROM taskTimeRequests_view ttr " +
            "JOIN tasks_view t ON ttr.task_id = t.id " +
            "JOIN sprints_view s ON t.sprint_id = s.id " +
            "JOIN projects_view p ON s.project_id = p.id " +
            "WHERE p.id =? AND ttr.status=?";

    private static final String OBJECT_TYPE = "object_types.taskTimeRequest";
    private static final String NEW_EST_TIME = "attributes.newEstimatedTime";
    private static final String STATUS = "attributes.status";
    private static final String TASK = "attributes.task";
    private static final String EMPLOYEE = "attributes.employee";

    public JdbcTaskTimeRequestDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<TaskTimeRequest> findAll() {
        return jdbcTemplate.executeQuery(new TaskTimeRequestMapper(), FIND_ALL_SQL);
    }

    @Override
    public TaskTimeRequest findById(Long id) {
        return jdbcTemplate.executeQuery(new TaskTimeRequestMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(TaskTimeRequest entity) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_NUMBER_SQL, entity.getNewEstimatedTime(),
                entity.getId(), METAMODEL_PROP.getProperty(NEW_EST_TIME));
        updateStatus(entity.getId(), entity.getStatus());
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, entity.getTask().getId(), entity.getId(),
                METAMODEL_PROP.getProperty(TASK));
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, entity.getEmployee().getId(), entity.getId(),
                METAMODEL_PROP.getProperty(EMPLOYEE));
    }

    @Override
    public Long add(TaskTimeRequest entity) {
        Long objectTypeId = Long.valueOf(METAMODEL_PROP.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);

        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_NUMBER_SQL, id,
                METAMODEL_PROP.getProperty(NEW_EST_TIME), entity.getNewEstimatedTime());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(STATUS), entity.getStatus().toString());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                METAMODEL_PROP.getProperty(TASK), entity.getTask().getId());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                METAMODEL_PROP.getProperty(EMPLOYEE), entity.getEmployee().getId());

        return id;
    }

    @Override
    public List<TaskTimeRequest> findAllByEmployeeId(Long id) {
        return jdbcTemplate.executeQuery(new TaskTimeRequestMapper(), FIND_BY_EMP_SQL, id);
    }

    @Override
    public List<TaskTimeRequest> findAllByTaskId(Long id) {
        return jdbcTemplate.executeQuery(new TaskTimeRequestMapper(), FIND_BY_TASK_SQL, id);
    }

    @Override
    public List<TaskTimeRequest> findAllByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new TaskTimeRequestMapper(), FIND_BY_PROJECT_SQL, id);
    }

    @Override
    public List<TaskTimeRequest> findByProjectId(Long id, TaskTimeRequest.Status status) {
        return jdbcTemplate.executeQuery(new TaskTimeRequestMapper(), FIND_SOME_BY_PROJECT_SQL,
                id, status.toString());
    }

    @Override
    public void updateStatus(Long taskTimeRequestId, TaskTimeRequest.Status status) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, status.toString(), taskTimeRequestId,
                METAMODEL_PROP.getProperty(STATUS));
    }
}
