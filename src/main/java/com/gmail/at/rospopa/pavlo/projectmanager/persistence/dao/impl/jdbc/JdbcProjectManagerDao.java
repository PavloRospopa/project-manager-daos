package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.ProjectManager;
import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.ProjectManagerDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.mapper.ProjectManagerMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;

import java.util.List;

public class JdbcProjectManagerDao extends AbstractUserJdbcDao<ProjectManager> implements ProjectManagerDao {
    private static final String FIND_ALL_SQL = "SELECT id, name, surname, username, password, email FROM " +
            "projectManagers_view";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, surname, username, password, email FROM " +
            "projectManagers_view WHERE id=?";
    private static final String FIND_BY_PROJECT_SQL = "SELECT id, name, surname, username, password, email " +
            "FROM projectManagers_view WHERE id=(SELECT project_manager_id FROM projects_view WHERE id=?)";

    private static final String OBJECT_TYPE = "object_types.projectManager";


    public JdbcProjectManagerDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Override
    public List<ProjectManager> findAll() {
        return jdbcTemplate.executeQuery(new ProjectManagerMapper(), FIND_ALL_SQL);
    }

    @Override
    public ProjectManager findById(Long id) {
        return jdbcTemplate.executeQuery(new ProjectManagerMapper(), FIND_BY_ID_SQL, id).get(0);
    }

    @Override
    public Long add(ProjectManager entity) {
        Long objectTypeId = Long.valueOf(METAMODEL_PROP.getProperty(OBJECT_TYPE));
        Long id = jdbcTemplate.executeInsert(INSERT_INTO_OBJECTS_SQL, PK_COLUMN_NAME, objectTypeId);
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(NAME), entity.getName());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(SURNAME), entity.getSurname());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(USERNAME), entity.getUsername());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(PASSWORD), entity.getPassword());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(EMAIL), entity.getEmail());
        jdbcTemplate.executeUpdate(INSERT_INTO_PARAMS_TEXT_SQL, id,
                METAMODEL_PROP.getProperty(ROLE), User.Role.PROJECT_MANAGER);

        return id;
    }

    @Override
    public ProjectManager findByProjectId(Long id) {
        return jdbcTemplate.executeQuery(new ProjectManagerMapper(), FIND_BY_PROJECT_SQL, id).get(0);
    }
}
