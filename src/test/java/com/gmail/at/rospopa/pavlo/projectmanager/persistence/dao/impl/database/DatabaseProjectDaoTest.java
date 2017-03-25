package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectDaoTest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.collections.PMCollectionsDatabase;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatabaseProjectDaoTest extends ProjectDaoTest {
    private PMCollectionsDatabase database;

    public DatabaseProjectDaoTest() {
        database = new PMCollectionsDatabase();
        database.initDatabase();

        projectDao = new DatabaseProjectDao(database);
    }

    @Before
    public void setUp() throws Exception {
        database.refreshDatabase();
    }


    @Test
    @Override
    public void addTest() {
        Project expectedProject = new Project(1L, "New Project", new Date(2017 - 1900, 2, 13), null,
                new Date(2017 - 1900, 8, 6), new Customer(1L), new ProjectManager(2L));

        projectDao.add(expectedProject);

        Project actualProject = projectDao.findById(1L);

        assertEquals(expectedProject, actualProject);
    }

    @Test
    @Override
    public void deleteTest() {
        Long projectId = 3L;

        projectDao.delete(projectId);
        Project deletedProject = projectDao.findById(projectId);

        assertNull(deletedProject);
    }
}