package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.database;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Sprint;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.Task;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.SprintDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;

import java.sql.Date;
import java.util.List;

public class DatabaseSprintDao extends AbstractDatabaseDao implements SprintDao {
    private static final String SPRINTS_TABLE = "SPRINTS";
    private static final String TASKS_TABLE = "TASKS";

    public DatabaseSprintDao(Database database) {
        super(database);
    }

    @Override
    public List<Sprint> findAll() {
        return selectFrom(SPRINTS_TABLE);
    }

    @Override
    public Sprint findById(Long id) {
        return database.selectFrom(SPRINTS_TABLE, id);
    }

    @Override
    public void delete(Long id) {
        database.deleteFrom(SPRINTS_TABLE, id);
    }

    @Override
    public void update(Sprint entity) {
        database.update(SPRINTS_TABLE, entity.getId(), entity);
    }

    @Override
    public Long add(Sprint entity) {
        Long id = database.getNextId(SPRINTS_TABLE);
        entity.setId(id);

        return database.add(SPRINTS_TABLE, entity);
    }

    @Override
    public List<Sprint> findByProjectId(Long id) {
        return selectFrom(SPRINTS_TABLE, s -> s.getProject().getId().equals(id));
    }

    @Override
    public Sprint findByTaskId(Long id) {
        Task task = database.selectFrom(TASKS_TABLE, id);
        if (task != null) {
            Long sprintId = task.getSprint().getId();

            return database.selectFrom(SPRINTS_TABLE, sprintId);
        }
        return null;
    }

    @Override
    public Sprint findActiveSprintByProjectId(Long id) {
        Date currentDate = new Date(new java.util.Date().getTime());

        return selectFrom(SPRINTS_TABLE,
                (Sprint s) -> s.getProject().getId().equals(id)
                        && s.getStartDate() != null
                        && s.getStartDate().compareTo(currentDate) < 0
                        && s.getCompletionDate() == null).get(0);
    }

    @Override
    public List<Sprint> findCompletedSprintsByProjectId(Long id) {
        return selectFrom(SPRINTS_TABLE,
                (Sprint s) -> s.getProject().getId().equals(id)
                        && s.getCompletionDate() != null);
    }
}
