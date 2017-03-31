package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskDelegation;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.TaskDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.DependenciesPair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseTaskDao extends AbstractDatabaseDao implements TaskDao {
    private static final String TASKS_TABLE = "TASKS";
    private static final String TASK_DELEGATIONS_TABLE = "TASK_DELEGATIONS";
    private static final String TASK_DEPENDENCIES_TABLE = "TASK_DEPENDENCIES";

    public DatabaseTaskDao(Database database) {
        super(database);
    }

    @Override
    public List<Task> findAll() {
        return selectFrom(TASKS_TABLE);
    }

    @Override
    public Task findById(Long id) {
        return database.selectFrom(TASKS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(TASKS_TABLE, id);
    }

    @Override
    public void update(Task entity) {
        database.update(TASKS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(Task entity) {
        Long id = database.getNextId(TASKS_TABLE);
        entity.setId(id);

        return database.add(TASKS_TABLE, entity);
    }

    @Override
    public List<Task> findAllChildTasks(Long id) {
        return selectFrom(TASKS_TABLE,
                (Task t) -> t.getParent() != null
                        && t.getParent().getId().equals(id));
    }

    @Override
    public Task findParentTask(Long id) {
        Task task = database.selectFrom(TASKS_TABLE, id);
        if (task.getParent() != null) {
            return database.selectFrom(TASKS_TABLE, task.getParent().getId());
        }
        return null;
    }

    @Override
    public List<Task> findAllBySprintId(Long id) {
        return selectFrom(TASKS_TABLE,
                (Task t) -> t.getSprint().getId().equals(id));
    }

    @Override
    public List<Task> findTasksBySprintId(Long id, Task.Status status) {
        return selectFrom(TASKS_TABLE,
                (Task t) -> t.getSprint().getId().equals(id)
                        && t.getStatus().equals(status));
    }

    @Override
    public List<Task> findAllByEmployeeId(Long id) {
        List<TaskDelegation> taskDelegationList = selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getEmployee().getId().equals(id));
        Set<Long> taskIdSet = taskDelegationList.stream()
                .map((TaskDelegation td) -> td.getTask().getId())
                .collect(Collectors.toSet());

        List<Task> taskList = new ArrayList<>();
        taskIdSet.forEach(taskId -> taskList.add(database.selectFrom(TASKS_TABLE, taskId)));
        return taskList;
    }

    @Override
    public List<Task> findTasksByEmployeeId(Long id, Task.Status status) {
        return findAllByEmployeeId(id)
                .stream()
                .filter((Task t) -> t.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Task.Status status, Long taskId) {
        Task task = findById(taskId);
        task.setStatus(status);

        database.update(TASKS_TABLE, taskId, task);
    }

    @Override
    public void updateEstimatedTime(int newEstimatedTime, Long taskId) {
        Task task = findById(taskId);
        task.setEstimatedTime(newEstimatedTime);

        database.update(TASKS_TABLE, taskId, task);
    }

    @Override
    public void updateSpentTime(int spentTime, Long taskId) {
        Task task = findById(taskId);
        task.setSpentTime(spentTime);

        database.update(TASKS_TABLE, taskId, task);
    }

    @Override
    public void updateParentTask(Long parentTaskId, Long taskId) {
        Task task = findById(taskId);
        task.setParent(new Task(parentTaskId));

        database.update(TASKS_TABLE, taskId, task);
    }

    @Override
    public List<Task> findDependantTasks(Long id) {
        List<DependenciesPair> dependenciesList = selectFrom(TASK_DEPENDENCIES_TABLE,
                (DependenciesPair pair) -> pair.getLeft().equals(id));
        Set<Long> taskIdSet = dependenciesList.stream()
                .map(Pair::getRight)
                .collect(Collectors.toSet());

        List<Task> taskList = new ArrayList<>();
        taskIdSet.forEach(taskId -> taskList.add(database.selectFrom(TASKS_TABLE, taskId)));
        return taskList;
    }

    @Override
    public List<Task> findDominatingTasks(Long id) {
        List<DependenciesPair> dependenciesList = selectFrom(TASK_DEPENDENCIES_TABLE,
                (DependenciesPair pair) -> pair.getRight().equals(id));
        Set<Long> taskIdSet = dependenciesList.stream()
                .map(Pair::getLeft)
                .collect(Collectors.toSet());

        List<Task> taskList = new ArrayList<>();
        taskIdSet.forEach(taskId -> taskList.add(database.selectFrom(TASKS_TABLE, taskId)));
        return taskList;
    }

    @Override
    public void addDependantTask(Long taskId, Long dependantTaskId) {
        DependenciesPair dependency = new DependenciesPair(taskId, dependantTaskId);

        database.add(TASK_DEPENDENCIES_TABLE, dependency);
    }

    @Override
    public void addDominatingTask(Long taskId, Long dominatingTaskId) {
        DependenciesPair dependency = new DependenciesPair(dominatingTaskId, taskId);

        database.add(TASK_DEPENDENCIES_TABLE, dependency);
    }

    @Override
    public void removeDependantTask(Long taskId, Long dependantTaskId) {
        Long dependencyId = database.selectFrom(TASK_DELEGATIONS_TABLE,
                (DependenciesPair pair) -> pair.getLeft().equals(taskId) &&
                        pair.getRight().equals(dependantTaskId))
                .keySet().iterator().next();

        database.deleteFrom(TASK_DEPENDENCIES_TABLE, dependencyId);
    }

    @Override
    public void removeDominatingTask(Long taskId, Long dominatingTaskId) {
        Long dependencyId = database.selectFrom(TASK_DELEGATIONS_TABLE,
                (DependenciesPair pair) -> pair.getLeft().equals(dominatingTaskId) &&
                        pair.getRight().equals(taskId))
                .keySet().iterator().next();

        database.deleteFrom(TASK_DEPENDENCIES_TABLE, dependencyId);
    }
}
