package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectDaoTest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.json.PMJsonDatabase;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.sql.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonDatabaseProjectDaoTest extends ProjectDaoTest {
    private static final String rootDirectoryPath = PropertiesLoader.getInstance()
            .getJsonDBProperties().getProperty("json.database.test.rootDirectoryPath");

    private PMJsonDatabase database;

    public JsonDatabaseProjectDaoTest() {
        database = new PMJsonDatabase(Paths.get(rootDirectoryPath), true);
        database.initDatabase();

        projectDao = new DatabaseProjectDao(database);
    }

    @Before
    public void setUp() throws Exception {
        database.fillDatabase();
    }

    @After
    public void tearDown() throws Exception {
        database.clearDatabase();
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