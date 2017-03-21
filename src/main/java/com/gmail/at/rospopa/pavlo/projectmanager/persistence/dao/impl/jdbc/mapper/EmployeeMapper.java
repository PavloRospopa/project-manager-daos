package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeMapper implements Mapper<Employee> {
    @Override
    public Employee map(ResultSet rs) throws SQLException {
        Employee employee = new Employee();

        employee.setId(rs.getLong("id"));
        employee.setName(rs.getString("name"));
        employee.setSurname(rs.getString("surname"));
        employee.setUsername(rs.getString("username"));
        employee.setPassword(rs.getString("password"));
        employee.setEmail(rs.getString("email"));
        employee.setRole(User.Role.EMPLOYEE);
        employee.setPosition(Employee.Position.valueOf(rs.getString("position")));

        return employee;
    }
}
