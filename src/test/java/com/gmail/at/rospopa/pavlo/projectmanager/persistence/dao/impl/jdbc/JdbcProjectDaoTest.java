package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectDaoTest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ScriptExecutant;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import com.gmail.at.rospopa.pavlo.projectmanager.util.ResourcesUtil;
import org.junit.*;

import java.sql.*;

import static org.junit.Assert.assertTrue;

public class JdbcProjectDaoTest extends ProjectDaoTest {

    private static final String JDBC_TEST_USER = "jdbc.test.user";
    private static final String JDBC_TEST_PASSWORD = "jdbc.test.password";
    private static Connection CONN;

    private ScriptExecutant scriptExecutant;

    @BeforeClass
    public static void getConnection() {
        String user = PropertiesLoader.getInstance().getDbProperties().getProperty(JDBC_TEST_USER);
        String password = PropertiesLoader.getInstance().getDbProperties().getProperty(JDBC_TEST_PASSWORD);
        CONN = ConnectionManager.getInstance().getConnection(user, password);
    }

    @AfterClass
    public static void closeConnection() {
        ConnectionManager.getInstance().close(CONN);
    }

    public JdbcProjectDaoTest() {
        scriptExecutant = new ScriptExecutant(CONN);

        scriptExecutant.executePLSQLScript(ResourcesUtil.getResourceFile("dropTablesSeqs.sql"));
        scriptExecutant.executeSQLScript(ResourcesUtil.getResourceFile("createDB.sql"));
        scriptExecutant.executePLSQLScript(ResourcesUtil.getResourceFile("createTriggers.sql"));

        projectDao = new JdbcProjectDao(CONN);
    }

    @Before
    public void setUp() {
        scriptExecutant.executeSQLScript(ResourcesUtil.getResourceFile("fillDB.sql"));
    }

    @After
    public void tearDown() {
        scriptExecutant.executeSQLScript(ResourcesUtil.getResourceFile("refreshDB.sql"));
    }
}

