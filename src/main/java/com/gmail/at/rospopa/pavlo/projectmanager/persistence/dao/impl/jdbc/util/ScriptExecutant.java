package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.exception.RuntimeSqlException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ScriptExecutant {
    private static final Logger LOGGER = LogManager.getLogger();

    private Connection connection;

    public ScriptExecutant(Connection connection) {
        this.connection = connection;
    }

    public boolean executeSQLScript(File file) {
        return executeScript(file, Pattern.compile("\\s*;\\s*"));
    }

    public boolean executePLSQLScript(File file) {
        return executeScript(file, Pattern.compile("\\s*/\\s*"));
    }

    private boolean executeScript(File file, Pattern pattern) {
        if (connection == null) {
            throw new RuntimeSqlException();
        }
        startTransaction();

        int stmtNum = 0;
        try (Scanner scanner = new Scanner(file).useDelimiter(pattern);
             Statement statement = connection.createStatement()) {
            while (scanner.hasNext()) {
                stmtNum++;
                statement.execute(scanner.next());
            }
            commit();
            return true;
        } catch (FileNotFoundException e) {
            LOGGER.error(String.format("Script file %s not found", file), e);
        } catch (SQLException e) {
            LOGGER.error(String.format("Error while executing SQL script at statement #%d in file %s",
                    stmtNum, file), e);
            rollback();
            throw new RuntimeSqlException();
        } finally {
            endTransaction();
        }
        return false;
    }

    private void startTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.error("Cannot start transaction mode", e);
            throw new RuntimeSqlException();
        }
    }

    private void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("Cannot commit changes to database", e);
            throw new RuntimeSqlException();
        }
    }

    private void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            LOGGER.error("Cannot rollback changes", e);
            throw new RuntimeSqlException();
        }
    }

    private void endTransaction() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.error("Cannot set auto transaction mode", e);
            throw new RuntimeSqlException();
        }
    }
}
