package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;

import java.util.List;

public interface TaskDao extends Dao<Long, Task> {
    List<Task> findAllChildTasks(Long id);
    Task findParentTask(Long id);

    List<Task> findAllBySprintId(Long id);
    List<Task> findTasksBySprintId(Long id, Task.Status status);

    List<Task> findAllByEmployeeId(Long id);
    List<Task> findTasksByEmployeeId(Long id, Task.Status status);

    void updateStatus(Task.Status status, Long taskId);
    void updateEstimatedTime(int newEstimatedTime, Long taskId);
    void updateSpentTime(int spentTime, Long taskId);
    void updateParentTask(Long parentTaskId, Long taskId);

    List<Task> findDependantTasks(Long id);
    List<Task> findDominatingTasks(Long id);

    void addDependantTask(Long taskId, Long dependantTaskId);
    void addDominatingTask(Long taskId, Long dominatingTaskId);

    void removeDependantTask(Long taskId, Long dependantTaskId);
    void removeDominatingTask(Long taskId, Long dominatingTaskId);
}
