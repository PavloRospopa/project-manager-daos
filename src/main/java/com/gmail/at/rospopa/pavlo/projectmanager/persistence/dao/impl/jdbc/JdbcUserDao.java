package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.UserDao;

import java.sql.Connection;
import java.util.List;

public class JdbcUserDao implements UserDao {

    private static final String FIND_USER_ID_SQL = "SELECT id, role FROM users_view WHERE username=? AND password=?";

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(Connection connection) {
        jdbcTemplate = new JdbcTemplate(connection);
    }

    @Override
    public User findByUsernamePassword(String username, String password) {
        List<String> results =
    }
}
