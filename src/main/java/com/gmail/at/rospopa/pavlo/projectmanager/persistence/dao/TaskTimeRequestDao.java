package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskTimeRequest;

import java.util.List;

public interface TaskTimeRequestDao extends Dao<Long, TaskTimeRequest> {
    List<TaskTimeRequest> findByTaskId(Long id);

    void updateStatus(Long taskTimeRequestId, TaskTimeRequest.Status status);
}
