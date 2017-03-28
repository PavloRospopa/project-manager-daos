package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;

abstract class AbstractUserJdbcDao<T extends User> extends AbstractJdbcDao {
    protected static final String NAME = "attributes.name";
    protected static final String SURNAME = "attributes.surname";
    protected static final String USERNAME = "attributes.username";
    protected static final String PASSWORD = "attributes.password";
    protected static final String EMAIL = "attributes.email";
    protected static final String ROLE = "attributes.role";

    public void update(T entity) {
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getName(), entity.getId(),
                METAMODEL_PROP.getProperty(NAME));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getSurname(), entity.getId(),
                METAMODEL_PROP.getProperty(SURNAME));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getUsername(), entity.getId(),
                METAMODEL_PROP.getProperty(USERNAME));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getPassword(), entity.getId(),
                METAMODEL_PROP.getProperty(PASSWORD));
        jdbcTemplate.executeUpdate(UPDATE_PARAMS_TEXT_SQL, entity.getEmail(), entity.getId(),
                METAMODEL_PROP.getProperty(EMAIL));
    }
}
