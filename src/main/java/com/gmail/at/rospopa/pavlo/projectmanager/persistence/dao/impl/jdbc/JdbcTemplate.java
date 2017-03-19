package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.exception.RuntimeSqlException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    private static final Logger LOGGER = LogManager.getLogger();

    private Connection connection;

    public JdbcTemplate(Connection connection) {
        this.connection = connection;
    }

    public <T> List<T> executeQuery(Mapper<T> mapper, String query, Object... params) {
        if (connection == null) {
            throw new RuntimeSqlException();
        }
        List<T> entities = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setParams(stmt, params);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                entities.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL exception during executing query to database", e);
            throw new RuntimeSqlException();
        }

        if (entities.isEmpty()) {
            entities.add(null);
        }
        return entities;
    }

    public Long executeInsert(String insertQuery, String columnName, Object... params) {
        if (connection == null) {
            throw new RuntimeSqlException();
        }

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery, new String[]{ columnName })) {
            setParams(stmt, params);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.error("SQL exception during inserting data to database", e);
            throw new RuntimeSqlException();
        }

        return null;
    }

    public int executeUpdate(String updateQuery, Object... params) {
        if (connection == null) {
            throw new RuntimeSqlException();
        }
        int updatedRows;

        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            setParams(stmt, params);
            updatedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("SQL exception during processing update query to database", e);
            throw new RuntimeSqlException();
        }

        return updatedRows;
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
