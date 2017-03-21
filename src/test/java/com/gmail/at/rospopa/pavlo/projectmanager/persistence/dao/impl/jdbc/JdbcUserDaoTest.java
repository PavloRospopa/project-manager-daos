package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.UserDaoTest;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ScriptExecutant;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.TestConnectionManager;
import com.gmail.at.rospopa.pavlo.projectmanager.util.ResourcesUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import java.sql.Connection;

public class JdbcUserDaoTest extends UserDaoTest {
    private ScriptExecutant scriptExecutant;

    private static Connection CONN = TestConnectionManager.getInstance().getConnection();

    @AfterClass
    public static void closeConn() {
        TestConnectionManager.getInstance().close(CONN);
    }

    public JdbcUserDaoTest() {
        scriptExecutant = new ScriptExecutant(CONN);

        scriptExecutant.executePLSQLScript(ResourcesUtil.getResourceFile("dropTablesSeqs.sql"));
        scriptExecutant.executeSQLScript(ResourcesUtil.getResourceFile("createDB.sql"));
        scriptExecutant.executePLSQLScript(ResourcesUtil.getResourceFile("createTriggers.sql"));

        userDao = new JdbcUserDao(TestConnectionManager.getInstance());
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