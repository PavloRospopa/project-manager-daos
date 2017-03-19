package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;

public interface ProjectManagerDao extends Dao<Long, ProjectManager> {
    ProjectManager findByProjectId(Long id);
}
