package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util;

import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;

import java.sql.Connection;
import java.util.Properties;

public class TestConnectionManager extends ConnectionManager {

    private static final TestConnectionManager INSTANCE = new TestConnectionManager();

    private static final String JDBC_TEST_USER = "jdbc.test.user";
    private static final String JDBC_TEST_PASSWORD = "jdbc.test.password";

    TestConnectionManager() {
    }

    public static TestConnectionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public Connection getConnection() {
        Properties dbProp = PropertiesLoader.getInstance().getDbProperties();

        String user = dbProp.getProperty(JDBC_TEST_USER);
        String password = dbProp.getProperty(JDBC_TEST_PASSWORD);
        return super.getConnection(user, password);
    }
}