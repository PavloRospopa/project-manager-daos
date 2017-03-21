package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util;

import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConnectionManager INSTANCE = new ConnectionManager();

    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASSWORD = "jdbc.password";

    ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        String user = PropertiesLoader.getInstance().getDbProperties().getProperty(JDBC_USER);
        String password = PropertiesLoader.getInstance().getDbProperties().getProperty(JDBC_PASSWORD);
        return getConnection(user, password);
    }

    public Connection getConnection(String user, String password) {
        Connection connection = null;
        try {
            String url = PropertiesLoader.getInstance().getDbProperties().getProperty(JDBC_URL);
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.error(String.format("Cannot obtain connection to the database. " +
                    "User: %s, password: %s", user, password), e);
        }
        return connection;
    }

    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot close connection to the database", e);
        }
    }
}
