package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.factory;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.JdbcProjectDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

public class JdbcDaoFactory implements DaoFactory {

    private final ConnectionManager connectionManager;

    public JdbcDaoFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public AdministratorDao getAdministratorDao() {
       return null;
    }

    @Override
    public CustomerDao getCustomerDao() {
        return null;
    }

    @Override
    public EmployeeDao getEmployeeDao() {
        return null;
    }

    @Override
    public ProjectDao getProjectDao() {
        return new JdbcProjectDao(connectionManager);
    }

    @Override
    public ProjectManagerDao getProjectManagerDao() {
        return null;
    }

    @Override
    public SprintDao getSprintDao() {
        return null;
    }

    @Override
    public TaskDao getTaskDao() {
        return null;
    }

    @Override
    public TaskDelegationDao getTaskDelegationDao() {
        return null;
    }

    @Override
    public TaskTimeRequestDao getTaskTimeRequestDao() {
        return null;
    }

    @Override
    public UserDao getUserDao() {
        return null;
    }
}
