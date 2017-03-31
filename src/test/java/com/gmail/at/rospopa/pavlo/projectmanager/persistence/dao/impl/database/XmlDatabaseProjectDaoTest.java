package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectDaoTest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.xml.PMXmlDatabase;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class XmlDatabaseProjectDaoTest extends ProjectDaoTest {
    private static final String rootDirectoryPath = PropertiesLoader.getInstance()
            .getXmlDBProperties().getProperty("xml.database.test.rootDirectoryPath");

    private PMXmlDatabase database;

    public XmlDatabaseProjectDaoTest() {
        database = new PMXmlDatabase(Paths.get(rootDirectoryPath), true);
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

    @Test
    public void test() {
        Set<Task> dominatingTaskSet = new HashSet<Task>() {{
                add(new Task(7L, 8, 7, null, new Sprint(4L), Employee.Position.JUNIOR,
                        "implement daos", "write dao classes to all entities in domain", Task.Status.COMPLETED));
                add(new Task(8L, 12, 14, null, new Sprint(4L), Employee.Position.MIDDLE,
                        "implement business layer", "write service classes", Task.Status.COMPLETED));
            }};

        Set<Task> actualDominatingTaskSet = new DatabaseTaskDao(database)
                .findDominatingTasks(9L)
                .stream()
                .collect(Collectors.toSet());

        assertEquals(dominatingTaskSet, actualDominatingTaskSet);
    }
}