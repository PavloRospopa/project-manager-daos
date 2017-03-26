package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskTimeRequest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.TaskTimeRequestDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseTaskTimeRequestDao extends AbstractDatabaseDao implements TaskTimeRequestDao {
    private static final String TASK_TIME_REQUESTS_TABLE = "TASK_TIME_REQUESTS";
    private static final String SPRINTS_TABLE = "SPRINTS";
    private static final String TASKS_TABLE = "TABLES";

    public DatabaseTaskTimeRequestDao(Database database) {
        super(database);
    }

    @Override
    public List<TaskTimeRequest> findAll() {
        return selectFrom(TASK_TIME_REQUESTS_TABLE);
    }

    @Override
    public TaskTimeRequest findById(Long id) {
        return database.selectFrom(TASK_TIME_REQUESTS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(TASK_TIME_REQUESTS_TABLE, id);
    }

    @Override
    public void update(TaskTimeRequest entity) {
        database.update(TASK_TIME_REQUESTS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(TaskTimeRequest entity) {
        Long id = database.getNextId(TASK_TIME_REQUESTS_TABLE);
        entity.setId(id);

        return database.add(TASK_TIME_REQUESTS_TABLE, entity);
    }

    @Override
    public List<TaskTimeRequest> findAllByEmployeeId(Long id) {
        return selectFrom(TASK_TIME_REQUESTS_TABLE,
                (TaskTimeRequest ttr) -> ttr.getEmployee().getId().equals(id));
    }

    @Override
    public List<TaskTimeRequest> findAllByTaskId(Long id) {
        return selectFrom(TASK_TIME_REQUESTS_TABLE,
                (TaskTimeRequest ttr) -> ttr.getTask().getId().equals(id));
    }

    @Override
    public List<TaskTimeRequest> findAllByProjectId(Long id) {
        Set<Long> sprintIdSet = database.selectFrom(SPRINTS_TABLE,
                (Sprint s) -> s.getProject().getId().equals(id))
                .keySet();

        Set<Long> taskIdSet = new HashSet<>();
        sprintIdSet.forEach(sprintId -> taskIdSet.addAll(database.selectFrom(TASKS_TABLE,
                (Task t) -> t.getSprint().getId().equals(sprintId)).keySet()));

        List<TaskTimeRequest> taskTimeRequestList = new ArrayList<>();
        taskIdSet.forEach(taskId -> taskTimeRequestList.addAll(selectFrom(TASK_TIME_REQUESTS_TABLE,
                (TaskTimeRequest ttr) -> ttr.getTask().getId().equals(taskId))));
        return taskTimeRequestList;
    }

    @Override
    public List<TaskTimeRequest> findByProjectId(Long id, TaskTimeRequest.Status status) {
        return findAllByProjectId(id)
                .stream()
                .filter((TaskTimeRequest ttr) -> ttr.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long taskTimeRequestId, TaskTimeRequest.Status status) {
        TaskTimeRequest taskTimeRequest = findById(taskTimeRequestId);
        taskTimeRequest.setStatus(status);

        database.update(TASK_TIME_REQUESTS_TABLE, taskTimeRequestId, taskTimeRequest);
    }
}
