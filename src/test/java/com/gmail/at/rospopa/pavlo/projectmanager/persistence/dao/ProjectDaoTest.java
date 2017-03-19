package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;
import org.junit.Test;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class ProjectDaoTest {

    protected ProjectDao projectDao;

    @Test
    public void findAllTest() {
        Project firstExpectedProject = new Project(3L, "First Project", new Date(2014 - 1900, 10, 22), new Date(2016 - 1900, 11, 31),
                new Date(2017 - 1900, 0, 6), new Customer(1L), new ProjectManager(2L));
        Project secondExpectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), new ProjectManager(2L));

        Set<Project> expectedProjects = new HashSet<>();
        expectedProjects.add(firstExpectedProject);
        expectedProjects.add(secondExpectedProject);

        Set<Project> actualProjects = projectDao.findAll().stream().collect(Collectors.toSet());

        assertEquals(expectedProjects, actualProjects);
    }

    @Test
    public void findByIdTest() {
        Project expectedProject = new Project(3L, "First Project", new Date(2014 - 1900, 10, 22), new Date(2016 - 1900, 11, 31),
                new Date(2017 - 1900, 0, 6), new Customer(1L), new ProjectManager(2L));

        Project actualProject = projectDao.findById(3L);

        assertEquals(expectedProject, actualProject);
    }

    @Test
    public void deleteTest() {
        Long projectId = 3L;

        projectDao.delete(projectId);
        Project deletedProject = projectDao.findById(projectId);

        assertNull(deletedProject);
    }

    @Test
    public void updateTest() {
        Project expectedProject = new Project(3L, "Updated project", new Date(2000 - 1900, 10, 22), new Date(2017 - 1900, 0, 1),
                new Date(2017 - 1900, 0, 6), new Customer(1L), new ProjectManager(2L));

        projectDao.update(expectedProject);

        Project actualProject = projectDao.findById(3L);

        assertEquals(expectedProject, actualProject);
    }

    @Test
    public void addTest() {
        Project expectedProject = new Project(28L, "New Project", new Date(2017 - 1900, 2, 13), null,
                new Date(2017 - 1900, 8, 6), new Customer(1L), new ProjectManager(2L));

        projectDao.add(expectedProject);

        Project actualProject = projectDao.findById(28L);

        assertEquals(expectedProject, actualProject);
    }

    @Test
    public void findByCustomerIdTest() {
        Project firstExpectedProject = new Project(3L, "First Project", new Date(2014 - 1900, 10, 22), new Date(2016 - 1900, 11, 31),
                new Date(2017 - 1900, 0, 6), new Customer(1L), new ProjectManager(2L));
        Project secondExpectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), new ProjectManager(2L));

        Set<Project> expectedProjects = new HashSet<>();
        expectedProjects.add(firstExpectedProject);
        expectedProjects.add(secondExpectedProject);

        Set<Project> actualProjects = projectDao.findByCustomerId(1L).stream().collect(Collectors.toSet());

        assertEquals(expectedProjects, actualProjects);
    }

    @Test
    public void findByProjectManagerIdTest() {
        Project firstExpectedProject = new Project(3L, "First Project", new Date(2014 - 1900, 10, 22), new Date(2016 - 1900, 11, 31),
                new Date(2017 - 1900, 0, 6), new Customer(1L), new ProjectManager(2L));
        Project secondExpectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), new ProjectManager(2L));

        Set<Project> expectedProjects = new HashSet<>();
        expectedProjects.add(firstExpectedProject);
        expectedProjects.add(secondExpectedProject);

        Set<Project> actualProjects = projectDao.findByProjectManagerId(2L).stream().collect(Collectors.toSet());

        assertEquals(expectedProjects, actualProjects);
    }

    @Test
    public void findBySprintIdTest() {
        Project expectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), new ProjectManager(2L));
        Long sprintId = 21L;

        Project actualProject = projectDao.findBySprintId(sprintId);

        assertEquals(expectedProject, actualProject);
    }

    @Test
    public void findActiveProjectsTest() {
        Project expectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), new ProjectManager(2L));

        Project actualProject = projectDao.findActiveProjects().get(0);

        assertEquals(expectedProject, actualProject);
    }

    @Test
    public void updateProjectManagerTest() {
        ProjectManager projectManager = new ProjectManager(27L);

        Project expectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), projectManager);

        projectDao.updateProjectManager(projectManager.getId(), expectedProject.getId());

        Project actualProject = projectDao.findById(expectedProject.getId());

        assertEquals(expectedProject, actualProject);
    }

    @Test
    public void updateCustomerTest() {
        Customer customer = new Customer(26L);

        Project expectedProject = new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                customer, new ProjectManager(2L));

        projectDao.updateCustomer(customer.getId(), expectedProject.getId());

        Project actualProject = projectDao.findById(expectedProject.getId());

        assertEquals(expectedProject, actualProject);
    }
}