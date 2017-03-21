package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;

import java.util.List;

public interface EmployeeDao extends Dao<Long, Employee> {
    List<Employee> findByTaskId(Long id);
    List<Employee> findByProjectId(Long id);

    void updatePosition(Employee.Position position, Long employeeId);
}
