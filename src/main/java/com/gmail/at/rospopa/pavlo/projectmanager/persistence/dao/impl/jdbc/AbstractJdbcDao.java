package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;

import java.util.Properties;

abstract class AbstractJdbcDao {

    private static final String DELETE_FROM_OBJECTS_SQL = "DELETE FROM objects WHERE object_id=?";
    private static final String DELETE_FROM_REFS_SQL = "DELETE FROM refs WHERE object_id=?";
    private static final String DELETE_FROM_PARAMS_SQL = "DELETE FROM params WHERE object_id=?";

    protected static final String INSERT_INTO_OBJECTS_SQL = "INSERT INTO objects(object_type_id) VALUES(?)";
    protected static final String INSERT_INTO_PARAMS_TEXT_SQL = "INSERT INTO params(object_id, attr_id, text_value) " +
            "VALUES(?, ?, ?)";
    protected static final String INSERT_INTO_PARAMS_DATE_SQL = "INSERT INTO params(object_id, attr_id, date_value) " +
            "VALUES(?, ?, ?)";
    protected static final String INSERT_INTO_PARAMS_NUMBER_SQL = "INSERT INTO params(object_id, attr_id, number_value) " +
            "VALUES(?, ?, ?)";
    protected static final String INSERT_INTO_REFS_SQL = "INSERT INTO refs(object_id, attr_id, reference) " +
            "VALUES(?, ?, ?)";

    protected static final String UPDATE_PARAMS_TEXT_SQL = "UPDATE params SET text_value=? WHERE object_id=? " +
            "AND attr_id=?";
    protected static final String UPDATE_PARAMS_DATE_SQL = "UPDATE params SET date_value=? WHERE object_id=? " +
            "AND attr_id=?";
    protected static final String UPDATE_PARAMS_NUMBER_SQL = "UPDATE params SET number_value=? WHERE object_id=? " +
            "AND attr_id=?";
    protected static final String UPDATE_REFS_SQL = "UPDATE refs SET reference=? WHERE object_id=? " +
            "AND attr_id=?";

    protected static final String PK_COLUMN_NAME = "object_id";

    protected static Properties METAMODEL_PROP = PropertiesLoader.getInstance().getMetamodelProperties();

    protected JdbcTemplate jdbcTemplate;


    public void delete(Long id) {
        jdbcTemplate.executeUpdate(DELETE_FROM_PARAMS_SQL, id);
        jdbcTemplate.executeUpdate(DELETE_FROM_REFS_SQL, id);
        jdbcTemplate.executeUpdate(DELETE_FROM_OBJECTS_SQL, id);
    }
}
