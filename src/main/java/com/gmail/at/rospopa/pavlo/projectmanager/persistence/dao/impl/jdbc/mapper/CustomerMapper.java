package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements Mapper<Customer> {
    @Override
    public Customer map(ResultSet rs) throws SQLException {
        Customer customer = new Customer();

        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setSurname(rs.getString("surname"));
        customer.setUsername(rs.getString("username"));
        customer.setPassword(rs.getString("password"));
        customer.setEmail(rs.getString("email"));
        customer.setRole(User.Role.CUSTOMER);
        customer.setCompany(rs.getString("company"));

        return customer;
    }
}
