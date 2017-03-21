package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskTimeRequest;

import java.util.List;

public interface TaskTimeRequestDao extends Dao<Long, TaskTimeRequest> {
    List<TaskTimeRequest> findAllByEmployeeId(Long id);
    List<TaskTimeRequest> findAllByTaskId(Long id);
    List<TaskTimeRequest> findAllByProjectId(Long id);
    List<TaskTimeRequest> findByProjectId(Long id, TaskTimeRequest.Status status);

    void updateStatus(Long taskTimeRequestId, TaskTimeRequest.Status status);
}
