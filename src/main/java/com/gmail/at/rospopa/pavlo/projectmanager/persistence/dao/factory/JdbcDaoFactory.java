package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.factory;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

public class JdbcDaoFactory implements DaoFactory {

    private final ConnectionManager connectionManager;

    public JdbcDaoFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public AdministratorDao getAdministratorDao() {
        return new JdbcAdministratorDao(connectionManager);
    }

    @Override
    public CustomerDao getCustomerDao() {
        return new JdbcCustomerDao(connectionManager);
    }

    @Override
    public EmployeeDao getEmployeeDao() {
        return new JdbcEmployeeDao(connectionManager);
    }

    @Override
    public ProjectDao getProjectDao() {
        return new JdbcProjectDao(connectionManager);
    }

    @Override
    public ProjectManagerDao getProjectManagerDao() {
        return new JdbcProjectManagerDao(connectionManager);
    }

    @Override
    public SprintDao getSprintDao() {
        return new JdbcSprintDao(connectionManager);
    }

    @Override
    public TaskDao getTaskDao() {
        return new JdbcTaskDao(connectionManager);
    }

    @Override
    public TaskDelegationDao getTaskDelegationDao() {
        return new JdbcTaskDelegationDao(connectionManager);
    }

    @Override
    public TaskTimeRequestDao getTaskTimeRequestDao() {
        return new JdbcTaskTimeRequestDao(connectionManager);
    }

    @Override
    public UserDao getUserDao() {
        return new JdbcUserDao(connectionManager);
    }
}
