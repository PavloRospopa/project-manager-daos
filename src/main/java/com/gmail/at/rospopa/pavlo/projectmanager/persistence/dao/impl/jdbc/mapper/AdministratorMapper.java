package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Administrator;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministratorMapper implements Mapper<Administrator> {
    @Override
    public Administrator map(ResultSet rs) throws SQLException {
        Administrator administrator = new Administrator();

        administrator.setId(rs.getLong("id"));
        administrator.setName(rs.getString("name"));
        administrator.setSurname(rs.getString("surname"));
        administrator.setUsername(rs.getString("username"));
        administrator.setPassword(rs.getString("password"));
        administrator.setEmail(rs.getString("email"));
        administrator.setRole(User.Role.ADMINISTRATOR);

        return administrator;
    }
}
