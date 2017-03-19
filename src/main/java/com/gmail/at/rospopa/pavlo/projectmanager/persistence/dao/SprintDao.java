package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;

import java.util.List;

public interface SprintDao extends Dao<Long, Sprint> {

    List<Sprint> findByProjectId(Long id);
    Sprint findByTaskId(Long id);
    Sprint findActiveSprintByProjectId(Long id);
    List<Sprint> findCompletedSprintsByProjectId(Long id);
}
