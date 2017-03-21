package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
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

}