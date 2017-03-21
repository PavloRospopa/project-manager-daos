package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskDelegation;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.TaskDelegationDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.TaskDelegationMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;


import java.sql.Timestamp;
import java.util.List;

public class JdbcTaskDelegationDao extends AbstractJdbcDao implements TaskDelegationDao {
    private static final String FIND_ALL_SQL = "SELECT id, startDateTime, completionDateTime, status, " +
            "task_id, employee_id FROM taskDelegations_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, startDateTime, completionDateTime, status, " +
            "task_id, employee_id FROM taskDelegations_view WHERE id=?";
    private static final String FIND_BY_EMP_SQL = "SELECT id, startDateTime, completionDateTime, status, " +
            "task_id, employee_id FROM taskDelegations_view WHERE employee_id=?";
    private static final String FIND_SOME_BY_EMP_SQL = "SELECT id, startDateTime, completionDateTime, status, " +
            "task_id, employee_id FROM taskDelegations_view WHERE employee_id=? AND status=?";

    private static final String FIND_BY_TASK_SQL = "SELECT id, startDateTime, completionDateTime, status, " +
            "task_id, employee_id FROM taskDelegations_view WHERE task_id=?";
    private static final String FIND_SOME_BY_TASK_SQL = "SELECT id, startDateTime, completionDateTime, status, " +
            "task_id, employee_id FROM taskDelegations_view WHERE task_id=? AND status=?";

    private static final String OBJECT_TYPE = "object_types.taskDelegation";
    private static final String START_DATE_TIME = "attributes.startDateTime";
    private static final String COMPLETION_DATE_TIME = "attributes.completionDateTime";
    private static final String STATUS = "attributes.status";
    private static final String TASK = "attributes.task";
    private static final String EMPLOYEE = "attributes.employee";

    public JdbcTaskDelegationDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<TaskDelegation> findAll() {
        return jdbcTemplate.executeQuery(new TaskDelegationMapper(), FIND_ALL_SQL);
    }

    @Override
    public TaskDelegation findById(Long id) {
        return jdbcTemplate.executeQuery(new TaskDelegationMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(TaskDelegation entity) {
        updateStartDateTime(entity.getId(), entity.getStartDateTime());
        updateCompletionDateTime(entity.getId(), entity.getCompletionDateTime());
        updateStatus(entity.getId(), entity.getStatus());
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, entity.getTask().getId(), entity.getId(),
                METAMODEL_PROP.getProperty(TASK));
        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, entity.getEmployee().getId(), entity.getId(),
                METAMODEL_PROP.getProperty(EMPLOYEE));
    }

    @Override
    public Long add(TaskDelegation entity) {
        Long objectTypeId = Long.valueOf(METAMODEL_PROP.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                METAMODEL_PROP.getProperty(START_DATE_TIME), entity.getStartDateTime());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, id,
                METAMODEL_PROP.getProperty(COMPLETION_DATE_TIME), entity.getCompletionDateTime());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(STATUS), entity.getStatus().toString());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                METAMODEL_PROP.getProperty(TASK), entity.getTask().getId());
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                METAMODEL_PROP.getProperty(EMPLOYEE), entity.getEmployee().getId());

        return id;
    }

    @Override
    public List<TaskDelegation> findAllByEmployeeId(Long id) {
        return jdbcTemplate.executeQuery(new TaskDelegationMapper(), FIND_BY_EMP_SQL, id);
    }

    @Override
    public List<TaskDelegation> findByEmployeeId(Long id, TaskDelegation.Status status) {
        return jdbcTemplate.executeQuery(new TaskDelegationMapper(), FIND_SOME_BY_EMP_SQL,
                id, status.toString());
    }

    @Override
    public List<TaskDelegation> findAllByTaskId(Long id) {
        return jdbcTemplate.executeQuery(new TaskDelegationMapper(), FIND_BY_TASK_SQL, id);
    }

    @Override
    public List<TaskDelegation> findByTaskId(Long id, TaskDelegation.Status status) {
        return jdbcTemplate.executeQuery(new TaskDelegationMapper(), FIND_SOME_BY_TASK_SQL,
                id, status.toString());
    }

    @Override
    public void updateStatus(Long taskDelegationId, TaskDelegation.Status status) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, status.toString(), taskDelegationId,
                METAMODEL_PROP.getProperty(STATUS));
    }

    @Override
    public void updateStartDateTime(Long taskDelegationId, Timestamp startDateTime) {
        int isUpdated = jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, startDateTime, taskDelegationId,
                METAMODEL_PROP.getProperty(START_DATE_TIME));
        if (isUpdated == 0) {
            jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, taskDelegationId,
                    METAMODEL_PROP.getProperty(START_DATE_TIME), startDateTime);
        }
    }

    @Override
    public void updateCompletionDateTime(Long taskDelegationId, Timestamp completionDateTime) {
        int isUpdated = jdbcTemplate.executeUpdate(UPDATE_PARAMS_DATE_SQL, completionDateTime, taskDelegationId,
                METAMODEL_PROP.getProperty(COMPLETION_DATE_TIME));
        if (isUpdated == 0) {
            jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_DATE_SQL, taskDelegationId,
                    METAMODEL_PROP.getProperty(COMPLETION_DATE_TIME), completionDateTime);
        }
    }
}
