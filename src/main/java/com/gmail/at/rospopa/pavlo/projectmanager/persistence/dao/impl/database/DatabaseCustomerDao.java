package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Project;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.CustomerDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.util.List;

public class DatabaseCustomerDao extends AbstractDatabaseDao implements CustomerDao {
    private static final String CUSTOMERS_TABLE = "CUSTOMERS";
    private static final String PROJECTS_TABLE = "PROJECTS";

    public DatabaseCustomerDao(Database database) {
        super(database);
    }

    @Override
    public Customer findByProjectId(Long id) {
        Project project = database.selectFrom(PROJECTS_TABLE, id);
        if (project != null && project.getCustomer() != null) {
            Long customerId = project.getCustomer().getId();

            return database.selectFrom(CUSTOMERS_TABLE, customerId);
        }
        return null;
    }

    @Override
    public List<Customer> findAll() {
        return selectFrom(CUSTOMERS_TABLE);
    }

    @Override
    public Customer findById(Long id) {
        return database.selectFrom(CUSTOMERS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(CUSTOMERS_TABLE, id);
    }

    @Override
    public void update(Customer entity) {
        database.update(CUSTOMERS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(Customer entity) {
        Long id = database.getNextId(CUSTOMERS_TABLE);
        entity.setId(id);

        return database.add(CUSTOMERS_TABLE, entity);
    }
}
