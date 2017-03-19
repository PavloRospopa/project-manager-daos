package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;

import java.util.List;

public interface TaskDao extends Dao<Long, Task> {
    List<Task> findAllChildTasks(Long id);
    Task findParentTask(Long id);

    List<Task> findBySprintId(Long id);
    List<Task> findUnassignedTasksBySprintId(Long id);
    List<Task> findActiveTasksBySprintId(Long id);
    List<Task> findCompletedTasksBySprintId(Long id);

    List<Task> findTasksByEmployeeId(Long id);
    List<Task> findActiveTasksByEmployeeId(Long id);
    List<Task> findCompletedTasksByEmployeeId(Long id);

    void updateStatus(Long taskId, Task.Status status);
    void updateEstimatedTime(Long taskId, int newEstimatedTime);
    void updateSpentTime(Long taskId, int spentTime);
}
