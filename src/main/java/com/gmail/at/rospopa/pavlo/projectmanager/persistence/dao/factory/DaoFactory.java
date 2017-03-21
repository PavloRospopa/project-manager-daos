package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.factory;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.*;

public interface DaoFactory {

    AdministratorDao getAdministratorDao();

    CustomerDao getCustomerDao();

    EmployeeDao getEmployeeDao();

    ProjectDao getProjectDao();

    ProjectManagerDao getProjectManagerDao();

    SprintDao getSprintDao();

    TaskDao getTaskDao();

    TaskDelegationDao getTaskDelegationDao();

    TaskTimeRequestDao getTaskTimeRequestDao();

    UserDao getUserDao();
}
