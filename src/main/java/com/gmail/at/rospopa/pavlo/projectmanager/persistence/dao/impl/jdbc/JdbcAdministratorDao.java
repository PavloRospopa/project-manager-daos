package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Administrator;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.AdministratorDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.AdministratorMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcAdministratorDao extends AbstractUserJdbcDao<Administrator> implements AdministratorDao {

    private static final String FIND_ALL_SQL = "SELECT id, name, surname, username, password, email FROM " +
            "administrators_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, surname, username, password, email FROM " +
            "administrators_view WHERE id=?";

    private static final String OBJECT_TYPE = "object_types.administrator";

    public JdbcAdministratorDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<Administrator> findAll() {
        return jdbcTemplate.executeQuery(new AdministratorMapper(), FIND_ALL_SQL);
    }

    @Override
    public Administrator findById(Long id) {
        return jdbcTemplate.executeQuery(new AdministratorMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public Long add(Administrator entity) {
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
                METAMODEL_PROP.getProperty(ROLE), User.Role.ADMINISTRATOR.toString());

        return id;
    }
}
