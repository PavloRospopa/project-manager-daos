package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;

import java.util.List;

public interface ProjectDao extends Dao<Long, Project> {

    List<Project> findByCustomerId(Long id);
    List<Project> findByProjectManagerId(Long id);
    Project findBySprintId(Long id);
    List<Project> findActiveProjects();

    void updateProjectManager(Long projectManagerId, Long projectId);
    void updateCustomer(Long customerId, Long projectId);
}
