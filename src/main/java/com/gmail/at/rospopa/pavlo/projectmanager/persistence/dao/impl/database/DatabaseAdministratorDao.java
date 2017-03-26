package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Administrator;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.AdministratorDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.util.List;

public class DatabaseAdministratorDao extends AbstractDatabaseDao implements AdministratorDao {
    private static final String ADMINISTRATORS_TABLE = "ADMINISTRATORS";

    public DatabaseAdministratorDao(Database database) {
        super(database);
    }

    @Override
    public List<Administrator> findAll() {
        return selectFrom(ADMINISTRATORS_TABLE);
    }

    @Override
    public Administrator findById(Long id) {
        return database.selectFrom(ADMINISTRATORS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(ADMINISTRATORS_TABLE, id);
    }

    @Override
    public void update(Administrator entity) {
        database.update(ADMINISTRATORS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(Administrator entity) {
        Long id = database.getNextId(ADMINISTRATORS_TABLE);
        entity.setId(id);

        return database.add(ADMINISTRATORS_TABLE, entity);
    }
}
