package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.TaskDelegation;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.EmployeeDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseEmployeeDao extends AbstractDatabaseDao implements EmployeeDao {
    private static final String EMPLOYEES_TABLE = "EMPLOYEES";
    private static final String TASK_DELEGATIONS_TABLE = "TASK_DELEGATIONS";
    private static final String SPRINTS_TABLE = "SPRINTS";
    private static final String TASKS_TABLE = "TABLES";

    public DatabaseEmployeeDao(Database database) {
        super(database);
    }

    @Override
    public List<Employee> findAll() {
        return selectFrom(EMPLOYEES_TABLE);
    }

    @Override
    public Employee findById(Long id) {
        return database.selectFrom(EMPLOYEES_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(EMPLOYEES_TABLE, id);
    }

    @Override
    public void update(Employee entity) {
        database.update(EMPLOYEES_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(Employee entity) {
        Long id = database.getNextId(EMPLOYEES_TABLE);
        entity.setId(id);

        return database.add(EMPLOYEES_TABLE, entity);
    }

    @Override
    public List<Employee> findByTaskId(Long id) {
        List<TaskDelegation> taskDelegationList = selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getTask().getId().equals(id));
        Set<Long> employeeIdSet = taskDelegationList.stream()
                .map((TaskDelegation td) -> td.getEmployee().getId())
                .collect(Collectors.toSet());

        List<Employee> employeeList = new ArrayList<>();
        employeeIdSet.forEach(employeeId -> employeeList.add(database.selectFrom(EMPLOYEES_TABLE, employeeId)));
        return employeeList;
    }

    @Override
    public List<Employee> findByProjectId(Long id) {
        Set<Long> sprintIdSet = database.selectFrom(SPRINTS_TABLE,
                (Sprint s) -> s.getProject().getId().equals(id))
                .keySet();

        Set<Long> taskIdSet = new HashSet<>();
        sprintIdSet.forEach(sprintId -> taskIdSet.addAll(database.selectFrom(TASKS_TABLE,
                (Task t) -> t.getSprint().getId().equals(sprintId)).keySet()));

        Set<Long> employeeIdSet = new HashSet<>();
        taskIdSet.forEach(taskId -> employeeIdSet.addAll(database.selectFrom(TASK_DELEGATIONS_TABLE,
                (TaskDelegation td) -> td.getTask().getId().equals(taskId))
                .values()
                .stream()
                .map((TaskDelegation td) -> td.getEmployee().getId())
                .collect(Collectors.toSet())));

        List<Employee> employeeList = new ArrayList<>();
        employeeIdSet.forEach(employeeId -> employeeList.add(database.selectFrom(EMPLOYEES_TABLE, employeeId)));
        return employeeList;
    }

    @Override
    public void updatePosition(Employee.Position position, Long employeeId) {
        Employee employee = findById(employeeId);
        employee.setPosition(position);

        database.update(EMPLOYEES_TABLE, employeeId, employee);
    }
}
