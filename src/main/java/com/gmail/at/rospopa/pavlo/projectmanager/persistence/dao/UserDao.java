package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.User;

public interface UserDao {

    User findByUsernamePassword(String username, String password);
}
