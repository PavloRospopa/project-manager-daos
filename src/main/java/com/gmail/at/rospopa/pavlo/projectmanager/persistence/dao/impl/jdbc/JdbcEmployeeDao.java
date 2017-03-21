package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Employee;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.EmployeeDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.EmployeeMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcEmployeeDao extends AbstractUserJdbcDao<Employee> implements EmployeeDao {
    private static final String FIND_ALL_SQL = "SELECT id, name, surname, username, password, email, position FROM " +
            "employees_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, surname, username, password, email, position FROM " +
            "employees_view WHERE id=?";
    private static final String FIND_BY_TASK_ID_SQL = "SELECT e.id, e.name, e.surname, e.username, e.password, e.email, " +
            "e.position FROM employees_view e JOIN taskDelegations_view t ON e.id = t.employee_id AND t.task_id =?";
    private static final String FIND_BY_PROJECT_ID_SQL = "SELECT DISTINCT e.id, e.name, e.surname, e.username, " +
            "e.password, e.email, e.position FROM employees_view e " +
            "JOIN taskDelegations_view td ON e.id = td.employee_id " +
            "JOIN tasks_view t ON td.task_id = t.id " +
            "JOIN sprints_view s ON t.sprint_id = s.id " +
            "JOIN projects_view p ON s.project_id = p.id AND p.id =?";

    private static final String OBJECT_TYPE = "object_types.employee";
    private static final String POSITION = "attributes.position";

    public JdbcEmployeeDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<Employee> findAll() {
        return jdbcTemplate.executeQuery(new EmployeeMapper(), FIND_ALL_SQL);
    }

    @Override
    public Employee findById(Long id) {
        return jdbcTemplate.executeQuery(new EmployeeMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(Employee entity) {
        super.update(entity);
        updatePosition(entity.getPosition(), entity.getId());
    }

    @Override
    public Long add(Employee entity) {
        Long objectTypeId = Long.valueOf(METAMODEL_PROP.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(NAME), entity.getName());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(SURNAME), entity.getSurname());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(USERNAME), entity.getUsername());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(PASSWORD), entity.getPassword());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(EMAIL), entity.getEmail());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(ROLE), User.Role.EMPLOYEE.toString());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(POSITION), entity.getPosition().toString());

        return id;
    }

    @Override
    public List<Employee> findByTaskId(Long id) {
        return jdbcTemplate.executeQuery(new EmployeeMapper(), FIND_BY_TASK_ID_SQL, id);
    }

    @Override
    public List<Employee> findByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new EmployeeMapper(), FIND_BY_PROJECT_ID_SQL, id);
    }

    @Override
    public void updatePosition(Employee.Position position, Long employeeId) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, position.toString(), employeeId,
                METAMODEL_PROP.getProperty(POSITION));
    }
}
