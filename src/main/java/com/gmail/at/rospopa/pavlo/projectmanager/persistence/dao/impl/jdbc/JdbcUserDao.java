package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.UserDao;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.factory.DaoFactory;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.factory.JdbcDaoFactory;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.util.ConnectionManager;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;


public class JdbcUserDao implements UserDao {

    private static final String FIND_USER_ID_ROLE_SQL = "SELECT id, role FROM users_view WHERE username=? " +
            "AND password=?";

    private JdbcTemplate jdbcTemplate;
    private DaoFactory daoFactory;

    public JdbcUserDao(ConnectionManager connectionManager) {
        jdbcTemplate = new JdbcTemplate(connectionManager);
        daoFactory = new JdbcDaoFactory(connectionManager);
    }

    @Override
    public User findByUsernamePassword(String username, String password) {
        Pair<Long, User.Role> idRolePair = jdbcTemplate.executeQuery(rs ->
                        new Pair<>(rs.getLong("id"), User.Role.valueOf(rs.getString("role"))),
                FIND_USER_ID_ROLE_SQL, username, password).get(0);

        Long userId = idRolePair.getLeft();

        if (userId == null) {
            return null;
        }

        User.Role userRole = idRolePair.getRight();
        switch (userRole){
            case ADMINISTRATOR:
                return daoFactory.getAdministratorDao().findById(userId);
            case PROJECT_MANAGER:
                return daoFactory.getProjectManagerDao().findById(userId);
            case CUSTOMER:
                return daoFactory.getCustomerDao().findById(userId);
            case EMPLOYEE:
                return daoFactory.getEmployeeDao().findById(userId);
        }

        return null;
    }
}
