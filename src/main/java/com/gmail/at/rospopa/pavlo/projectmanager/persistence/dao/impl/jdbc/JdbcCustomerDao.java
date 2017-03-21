package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.CustomerDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.CustomerMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcCustomerDao extends AbstractUserJdbcDao<Customer> implements CustomerDao {

    private static final String FIND_ALL_SQL = "SELECT id, name, surname, username, password, email, company FROM " +
            "customers_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, surname, username, password, email, company FROM " +
            "customers_view WHERE id=?";
    private static final String FIND_BY_PROJECT_ID_SQL = "SELECT id, name, surname, username, password, email, company " +
            "FROM customers_view WHERE id=(SELECT customer_id FROM projects_view WHERE id=?)";

    private static final String OBJECT_TYPE = "object_types.customer";
    private static final String COMPANY = "attributes.company";

    public JdbcCustomerDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public Customer findByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new CustomerMapper(), FIND_BY_PROJECT_ID_SQL, id).get(0);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.executeQuery(new CustomerMapper(), FIND_ALL_SQL);
    }

    @Override
    public Customer findById(Long id) {
        return jdbcTemplate.executeQuery(new CustomerMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public void update(Customer entity) {
        super.update(entity);
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getCompany(), entity.getId(),
                METAMODEL_PROP.getProperty(COMPANY));
    }

    @Override
    public Long add(Customer entity) {
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
                METAMODEL_PROP.getProperty(ROLE), User.Role.CUSTOMER);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(COMPANY), entity.getCompany());

        return id;
    }
}
