package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.xml;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.*;

import java.nio.file.Path;
import java.sql.Date;
import java.sql.Timestamp;

public class PMXmlDatabase extends XmlDatabase {
    private static final String PROJECTS_TABLE = "PROJECTS";
    private static final String SPRINTS_TABLE = "SPRINTS";
    private static final String TASKS_TABLE = "TASKS";
    private static final String TASK_DELEGATIONS_TABLE = "TASK_DELEGATIONS";
    private static final String TASK_TIME_REQUESTS_TABLE = "TASK_TIME_REQUESTS";
    private static final String ADMINISTRATORS_TABLE = "ADMINISTRATORS";
    private static final String CUSTOMERS_TABLE = "CUSTOMERS";
    private static final String EMPLOYEES_TABLE = "EMPLOYEES";
    private static final String PROJECT_MANAGERS_TABLE = "PROJECT_MANAGERS";
    private static final String TASK_DEPENDENCIES_TABLE = "TASK_DEPENDENCIES";

    public PMXmlDatabase(Path rootDirectoryPath, boolean rewriteOldData) {
        super(rootDirectoryPath, rewriteOldData);
    }

    public PMXmlDatabase(Path rootDirectoryPath) {
        super(rootDirectoryPath);
    }

    @Override
    public void initDatabase() {
        super.initDatabase();

        createTable(PROJECTS_TABLE, Project.class);
        createTable(SPRINTS_TABLE, Sprint.class);
        createTable(TASKS_TABLE, Task.class);
        createTable(TASK_DELEGATIONS_TABLE, TaskDelegation.class);
        createTable(TASK_TIME_REQUESTS_TABLE, TaskTimeRequest.class);
        createTable(ADMINISTRATORS_TABLE, Administrator.class);
        createTable(EMPLOYEES_TABLE, Employee.class);
        createTable(CUSTOMERS_TABLE, Customer.class);
        createTable(PROJECT_MANAGERS_TABLE, ProjectManager.class);
        createTable(TASK_DEPENDENCIES_TABLE, DependenciesPair.class);
    }


    public void clearDatabase() {
        getTableNames().forEach(this::clearTable);
    }

    public void fillDatabase() {
        addProjects();
        addSprints();
        addTasks();
        addTaskDelegations();
        addTaskTimeRequests();
        addAdministrators();
        addCustomers();
        addProjectManagers();
        addEmployees();
        addDependencies();
    }

    private void addProjects() {
        insert(PROJECTS_TABLE, 3L, new Project(3L, "First Project", new Date(2014 - 1900, 10, 22), new Date(2016 - 1900, 11, 31),
                new Date(2017 - 1900, 0, 6), new Customer(1L), new ProjectManager(2L)));
        insert(PROJECTS_TABLE, 20L, new Project(20L, "VideoChat", new Date(2017 - 1900, 2, 12), null, new Date(2017 - 1900, 5, 12),
                new Customer(1L), new ProjectManager(2L)));
    }

    private void addSprints() {
        insert(SPRINTS_TABLE, 4L, new Sprint(4L, "1st Sprint of First project", new Date(2014 - 1900, 10, 23),
                new Date(2015 - 1900, 5, 27), new Date(2015 - 1900, 5, 30), null, new Project(3L)));
        insert(SPRINTS_TABLE, 5L, new Sprint(5L, "2nd Sprint of First project", new Date(2015 - 1900, 5, 28),
                new Date(2016 - 1900, 2, 8), new Date(2016 - 1900, 2, 6), new Sprint(4L), new Project(3L)));
        insert(SPRINTS_TABLE, 6L, new Sprint(6L, "3rd Sprint of First project", new Date(2016 - 1900, 2, 8),
                new Date(2016 - 1900, 11, 31), new Date(2017 - 1900, 0, 6), new Sprint(5L), new Project(3L)));
        insert(SPRINTS_TABLE, 21L, new Sprint(21L, "VideoChat`s 1st Sprint", new Date(2017 - 1900, 2, 12),
                null, new Date(2017 - 1900, 3, 15), null, new Project(20L)));
    }

    private void addTasks() {
        insert(TASKS_TABLE, 7L, new Task(7L, 8, 7, null, new Sprint(4L), Employee.Position.JUNIOR,
                "implement daos", "write dao classes to all entities in domain", Task.Status.COMPLETED));
        insert(TASKS_TABLE, 8L, new Task(8L, 12, 14, null, new Sprint(4L), Employee.Position.MIDDLE,
                "implement business layer", "write service classes", Task.Status.COMPLETED));
        insert(TASKS_TABLE, 9L, new Task(9L, 10, 10, null, new Sprint(5L), Employee.Position.MIDDLE,
                "implement presentation layer", "write controller and jsps", Task.Status.COMPLETED));
        insert(TASKS_TABLE, 10L, new Task(10L, 6, 5, null, new Sprint(6L), Employee.Position.JUNIOR,
                "enhance front end", "add js to pages", Task.Status.COMPLETED));
        insert(TASKS_TABLE, 22L, new Task(22L, 10, 0, null, new Sprint(21L), Employee.Position.MIDDLE,
                "Develop architecture", "develop Videochat`s architecture", Task.Status.ACTIVE));
        insert(TASKS_TABLE, 23L, new Task(23L, 6, 0, null, new Sprint(21L), Employee.Position.JUNIOR,
                "design website appearance", "design website appearance", Task.Status.UNASSIGNED));
    }

    private void addTaskDelegations() {
        insert(TASK_DELEGATIONS_TABLE, 13L, new TaskDelegation(13L, new Task(7L), new Employee(11L),
                new Timestamp(2014 - 1900, 10, 23, 9, 0, 0, 0), new Timestamp(2014 - 1900, 10, 23, 16, 0, 0, 0),
                TaskDelegation.Status.COMPLETED));
        insert(TASK_DELEGATIONS_TABLE, 14L, new TaskDelegation(14L, new Task(8L), new Employee(12L),
                new Timestamp(2015 - 1900, 0, 9, 8, 0, 0, 0), new Timestamp(2015 - 1900, 0, 10, 11, 0, 0, 0),
                TaskDelegation.Status.COMPLETED));
        insert(TASK_DELEGATIONS_TABLE, 15L, new TaskDelegation(15L, new Task(9L), new Employee(12L),
                new Timestamp(2015 - 1900, 5, 29, 8, 0, 0, 0), new Timestamp(2015 - 1900, 5, 29, 18, 0, 0, 0),
                TaskDelegation.Status.COMPLETED));
        insert(TASK_DELEGATIONS_TABLE, 16L, new TaskDelegation(16L, new Task(10L), new Employee(11L),
                new Timestamp(2016 - 1900, 11, 30, 8, 0, 0, 0), new Timestamp(2016 - 1900, 11, 30, 13, 0, 0, 0),
                TaskDelegation.Status.COMPLETED));
        insert(TASK_DELEGATIONS_TABLE, 17L, new TaskDelegation(17L, new Task(10L), new Employee(12L),
                new Timestamp(2016 - 1900, 11, 30, 8, 0, 0, 0), new Timestamp(2016 - 1900, 11, 30, 12, 50, 0, 0),
                TaskDelegation.Status.COMPLETED));
        insert(TASK_DELEGATIONS_TABLE, 24L, new TaskDelegation(24L, new Task(22L), new Employee(12L),
                new Timestamp(2017 - 1900, 2, 12, 16, 0, 0, 0), null, TaskDelegation.Status.ACTIVE));
        insert(TASK_DELEGATIONS_TABLE, 25L, new TaskDelegation(25L, new Task(23L), new Employee(11L),
                null, null, TaskDelegation.Status.UNCONFIRMED));
    }

    private void addTaskTimeRequests() {
        insert(TASK_TIME_REQUESTS_TABLE, 18L, new TaskTimeRequest(18L, new Task(10L), new Employee(12L),
                15, TaskTimeRequest.Status.REFUSED));
    }

    private void addProjectManagers() {
        insert(PROJECT_MANAGERS_TABLE, 27L, new ProjectManager(27L, "Denis", "Wolf", "wolfmanager", "manager",
                "wolf@mail.ru", User.Role.PROJECT_MANAGER));
        insert(PROJECT_MANAGERS_TABLE, 2L, new ProjectManager(2L, "Mike", "Dubovski", "prmanager", "pr1",
                "kot@mail.ru", User.Role.PROJECT_MANAGER));
    }

    private void addCustomers() {
        insert(CUSTOMERS_TABLE, 26L, new Customer(26L, "Semen", "Rodan", "srodan", "rodanrodan", "rodan@gmail.com",
                User.Role.CUSTOMER, "Space X"));
        insert(CUSTOMERS_TABLE, 1L, new Customer(1L, "Petro", "Timoshenko", "piratuk", "some_password",
                "sobaka@gmail.com", User.Role.CUSTOMER, "Taggart Transcontinental"));
    }

    private void addAdministrators() {
        insert(ADMINISTRATORS_TABLE, 19L, new Administrator(19L, "Anton", "Pupkin", "admin", "admin111",
                "admin@mail.ru", User.Role.ADMINISTRATOR));
    }

    private void addEmployees() {
        insert(EMPLOYEES_TABLE, 12L, new Employee(12L, "Ray", "Fuko", "rayfuko", "rayray", "fuko@gmail.com",
                User.Role.EMPLOYEE, Employee.Position.MIDDLE));
        insert(EMPLOYEES_TABLE, 11L, new Employee(11L, "Robert", "Kolezki", "kolezki", "111", "kolezki@gmail.com",
                User.Role.EMPLOYEE, Employee.Position.JUNIOR));
    }

    private void addDependencies() {
        insert(TASK_DEPENDENCIES_TABLE, 1L, new DependenciesPair(7L, 9L));
        insert(TASK_DEPENDENCIES_TABLE, 2L, new DependenciesPair(8L, 9L));
        insert(TASK_DEPENDENCIES_TABLE, 3L, new DependenciesPair(22L, 23L));
    }
}