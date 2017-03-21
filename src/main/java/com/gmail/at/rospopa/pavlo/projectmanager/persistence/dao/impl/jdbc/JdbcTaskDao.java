package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.TaskDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.TaskMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcTaskDao extends AbstractJdbcDao implements TaskDao {
    private static final String FIND_ALL_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view WHERE id=?";
    private static final String FIND_CHILD_TASKS_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view WHERE parentTask_id=?";
    private static final String FIND_PARENT_TASK_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view " +
            "WHERE id = (SELECT parentTask_id FROM tasks_view WHERE id =?)";
    private static final String FIND_BY_SPRINT_ID_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view WHERE sprint_id=?";
    private static final String FIND_SOME_BY_SPRINT_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view WHERE sprint_id=? AND status=?";
    private static final String FIND_BY_EMP_ID_SQL = "SELECT t.id, t.name, t.estimatedTime, t.spentTime, " +
            "t.requiredEmpPosition, t.description, t.status, t.parentTask_id, t.sprint_id " +
            "FROM tasks_view t " +
            "JOIN taskDelegations_view td ON t.id = td.task_id AND td.employee_id =?";
    private static final String FIND_SOME_BY_EMP_SQL = "SELECT t.id, t.name, t.estimatedTime, t.spentTime, " +
            "t.requiredEmpPosition, t.description, t.status, t.parentTask_id, t.sprint_id " +
            "FROM tasks_view t " +
            "JOIN taskDelegations_view td ON t.id = td.task_id AND td.employee_id =? " +
            "WHERE t.status=?";
    private static final String FIND_DEPENDANT_TASKS_SQL = "SELECT id, name, estimatedTime, spentTime, requiredEmpPosition, " +
            "description, status, parentTask_id, sprint_id FROM tasks_view WHERE id IN " +
            "(SELECT dep.reference FROM tasks_view t " +
            "JOIN refs dep ON t.id = dep.object_id AND dep.attr_id =? " +
            "WHERE t.id =?)";

    private static final String FIND_DOMINATING_TASKS_SQL = "SELECT id, name, estimatedTime, spentTime, " +
            "requiredEmpPosition, description, status, parentTask_id, sprint_id FROM tasks_view " +
            "WHERE id IN (SELECT dep.object_id FROM tasks_view t " +
            "JOIN refs dep ON t.id = dep.reference AND dep.attr_id =? " +
            "WHERE t.id =?)";

    private static final String DELETE_DEPENDENCY_SQL = "DELETE FROM refs WHERE object_id=? AND " +
            "attr_id=? AND reference=?";


    private static final String OBJECT_TYPE = "object_types.task";
    private static final String NAME = "attributes.name";
    private static final String ESTIMATED_TIME = "attributes.estimatedTime";
    private static final String SPENT_TIME = "attributes.spentTime";
    private static final String REQUIRED_EMP_POS = "attributes.requiredEmpPosition";
    private static final String DESCRIPTION = "attributes.description";
    private static final String STATUS = "attributes.status";
    private static final String PARENT = "attributes.parent";
    private static final String SPRINT = "attributes.sprint";
    private static final String TASK_DEPENDENCY = "attributes.taskDependency";

    public JdbcTaskDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<Task> findAll() {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_ALL_SQL);
    }

    @Override
    public Task findById(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(Task entity) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getName(), entity.getId(),
                METAMODEL_PROP.getProperty(NAME));
        updateEstimatedTime(entity.getEstimatedTime(), entity.getId());
        updateSpentTime(entity.getSpentTime(), entity.getId());
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getRequiredEmpPosition().toString(), entity.getId(),
                METAMODEL_PROP.getProperty(REQUIRED_EMP_POS));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getDescription(), entity.getId(),
                METAMODEL_PROP.getProperty(DESCRIPTION));
        updateStatus(entity.getStatus(), entity.getId());

        Long parentId = entity.getParent() != null ? entity.getParent().getId() : null;
        updateParentTask(parentId, entity.getId());

        jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, entity.getSprint().getId(), entity.getId(),
                METAMODEL_PROP.getProperty(SPRINT));
    }

    @Override
    public Long add(Task entity) {
        Long objectTypeId = Long.valueOf(METAMODEL_PROP.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(NAME), entity.getName());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_NUMBER_SQL, id,
                METAMODEL_PROP.getProperty(ESTIMATED_TIME), entity.getEstimatedTime());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(REQUIRED_EMP_POS), entity.getRequiredEmpPosition());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(DESCRIPTION), entity.getDescription());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(STATUS), Task.Status.UNASSIGNED);
        Task parent = entity.getParent();
        if (parent != null) {
            jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                    METAMODEL_PROP.getProperty(PARENT), parent.getId());
        }
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, id,
                METAMODEL_PROP.getProperty(SPRINT), entity.getSprint().getId());

        return id;
    }

    @Override
    public List<Task> findAllChildTasks(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_CHILD_TASKS_SQL, id);
    }

    @Override
    public Task findParentTask(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_PARENT_TASK_SQL, id).get(0);
    }

    @Override
    public List<Task> findAllBySprintId(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_BY_SPRINT_ID_SQL, id);
    }

    @Override
    public List<Task> findTasksBySprintId(Long id, Task.Status status) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_SOME_BY_SPRINT_SQL, id,
                status.toString());
    }

    @Override
    public List<Task> findAllByEmployeeId(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_BY_EMP_ID_SQL, id);
    }

    @Override
    public List<Task> findTasksByEmployeeId(Long id, Task.Status status) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_SOME_BY_EMP_SQL, id,
                status.toString());
    }

    @Override
    public void updateStatus(Task.Status status, Long taskId) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, status.toString(), taskId,
                METAMODEL_PROP.getProperty(STATUS));
    }

    @Override
    public void updateEstimatedTime(int newEstimatedTime, Long taskId) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_NUMBER_SQL, newEstimatedTime, taskId,
                METAMODEL_PROP.getProperty(ESTIMATED_TIME));
    }

    @Override
    public void updateSpentTime(int spentTime, Long taskId) {
        int isUpdated = jdbcTemplate.executeUpdate(UPDATE_PARAMS_NUMBER_SQL, spentTime, taskId,
                METAMODEL_PROP.getProperty(SPENT_TIME));
        if (isUpdated == 0) {
            jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_NUMBER_SQL, taskId,
                    METAMODEL_PROP.getProperty(SPENT_TIME), spentTime);
        }
    }

    @Override
    public void updateParentTask(Long parentTaskId, Long taskId) {
        int isUpdated = jdbcTemplate.executeUpdate(UPDATE_REFS_SQL, parentTaskId, taskId,
                METAMODEL_PROP.getProperty(PARENT));
        if (isUpdated == 0) {
            jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, taskId,
                    METAMODEL_PROP.getProperty(PARENT), parentTaskId);
        }
    }

    @Override
    public List<Task> findDependantTasks(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_DEPENDANT_TASKS_SQL,
                METAMODEL_PROP.getProperty(TASK_DEPENDENCY), id);
    }

    @Override
    public List<Task> findDominatingTasks(Long id) {
        return jdbcTemplate.executeQuery(new TaskMapper(), FIND_DOMINATING_TASKS_SQL,
                METAMODEL_PROP.getProperty(TASK_DEPENDENCY), id);
    }

    @Override
    public void addDependantTask(Long taskId, Long dependantTaskId) {
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, taskId,
                METAMODEL_PROP.getProperty(TASK_DEPENDENCY), dependantTaskId);
    }

    @Override
    public void addDominatingTask(Long taskId, Long dominatingTaskId) {
        jdbcTemplate.executeUpdate(INSERT_INTO_REFS_SQL, dominatingTaskId,
                METAMODEL_PROP.getProperty(TASK_DEPENDENCY), taskId);
    }

    @Override
    public void removeDependantTask(Long taskId, Long dependantTaskId) {
        jdbcTemplate.executeUpdate(DELETE_DEPENDENCY_SQL, taskId,
                METAMODEL_PROP.getProperty(TASK_DEPENDENCY), dependantTaskId);
    }

    @Override
    public void removeDominatingTask(Long taskId, Long dominatingTaskId) {
        jdbcTemplate.executeUpdate(DELETE_DEPENDENCY_SQL, dominatingTaskId,
                METAMODEL_PROP.getProperty(TASK_DEPENDENCY), taskId);
    }
}
