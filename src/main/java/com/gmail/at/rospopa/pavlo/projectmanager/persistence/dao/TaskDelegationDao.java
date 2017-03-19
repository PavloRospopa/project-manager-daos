package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskDelegation;

import java.sql.Timestamp;
import java.util.List;

public interface TaskDelegationDao extends Dao<Long, TaskDelegation> {
    List<TaskDelegation> findByEmployeeId(Long id);
    List<TaskDelegation> findByTaskId(Long id);

    void updateStatus(Long taskDelegationId, TaskDelegation.Status status);
    void updateStartDateTime(Long taskDelegationId, Timestamp startDateTime);
    void updateCompletionDateTime(Long taskDelegationId, Timestamp completionDateTime);
}
