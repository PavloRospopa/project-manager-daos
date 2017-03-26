package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskDelegation;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.TaskDelegationDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.sql.Timestamp;
import java.util.List;

public class DatabaseTaskDelegationDao extends AbstractDatabaseDao implements TaskDelegationDao {
    private static final String TASK_DELEGATIONS_TABLE = "TASK_DELEGATIONS";

    public DatabaseTaskDelegationDao(Database database) {
        super(database);
    }

    @Override
    public List<TaskDelegation> findAll() {
        return selectFrom(TASK_DELEGATIONS_TABLE);
    }

    @Override
    public TaskDelegation findById(Long id) {
        return database.selectFrom(TASK_DELEGATIONS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(TASK_DELEGATIONS_TABLE, id);
    }

    @Override
    public void update(TaskDelegation entity) {
        database.update(TASK_DELEGATIONS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(TaskDelegation entity) {
        Long id = database.getNextId(TASK_DELEGATIONS_TABLE);
        entity.setId(id);

        return database.add(TASK_DELEGATIONS_TABLE, entity);
    }

    @Override
    public List<TaskDelegation> findAllByEmployeeId(Long id) {
        return selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getEmployee().getId().equals(id));
    }

    @Override
    public List<TaskDelegation> findByEmployeeId(Long id, TaskDelegation.Status status) {
        return selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getEmployee().getId().equals(id)
                        && td.getStatus().equals(status));
    }

    @Override
    public List<TaskDelegation> findAllByTaskId(Long id) {
        return selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getTask().getId().equals(id));
    }

    @Override
    public List<TaskDelegation> findByTaskId(Long id, TaskDelegation.Status status) {
        return selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getTask().getId().equals(id)
                        && td.getStatus().equals(status));
    }

    @Override
    public void updateStatus(Long taskDelegationId, TaskDelegation.Status status) {
        TaskDelegation taskDelegation = findById(taskDelegationId);
        taskDelegation.setStatus(status);

        database.update(TASK_DELEGATIONS_TABLE, taskDelegationId, taskDelegation);
    }

    @Override
    public void updateStartDateTime(Long taskDelegationId, Timestamp startDateTime) {
        TaskDelegation taskDelegation = findById(taskDelegationId);
        taskDelegation.setStartDateTime(startDateTime);

        database.update(TASK_DELEGATIONS_TABLE, taskDelegationId, taskDelegation);
    }

    @Override
    public void updateCompletionDateTime(Long taskDelegationId, Timestamp completionDateTime) {
        TaskDelegation taskDelegation = findById(taskDelegationId);
        taskDelegation.setCompletionDateTime(completionDateTime);

        database.update(TASK_DELEGATIONS_TABLE, taskDelegationId, taskDelegation);
    }
}
