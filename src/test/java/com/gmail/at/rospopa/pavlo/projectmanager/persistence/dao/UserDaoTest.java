package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.*;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class UserDaoTest {

    protected UserDao userDao;

    @Test
    public void findEmployeeByUsernamePassword() {
        Employee expectedEmployee = new Employee(12L, "Ray", "Fuko", "rayfuko", "rayray", "fuko@gmail.com",
                User.Role.EMPLOYEE, Employee.Position.MIDDLE);

        User actualEmployee = userDao.findByUsernamePassword("rayfuko", "rayray");

        assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    public void findCustomerByUsernamePassword() {
        Customer expectedCustomer = new Customer(26L, "Semen", "Rodan", "srodan", "rodanrodan", "rodan@gmail.com",
                User.Role.CUSTOMER, "Space X");

        User actualCustomer = userDao.findByUsernamePassword("srodan", "rodanrodan");

        assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    public void findAdministratorByUsernamePassword() {
        Administrator expectedAdministrator = new Administrator(19L, "Anton", "Pupkin", "admin", "admin111", "admin@mail.ru",
                User.Role.ADMINISTRATOR);

        User actualAdministrator = userDao.findByUsernamePassword("admin", "admin111");

        assertEquals(expectedAdministrator, actualAdministrator);
    }

    @Test
    public void findProjectManagerByUsernamePassword() {
        ProjectManager expectedManager = new ProjectManager(2L, "Mike", "Dubovski", "prmanager", "pr1", "kot@mail.ru",
                User.Role.PROJECT_MANAGER);

        User actualManager = userDao.findByUsernamePassword("prmanager", "pr1");

        assertEquals(expectedManager, actualManager);
    }
}