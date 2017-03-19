package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao;

import com.gmail.at.rospopa.pavlo.projectmanager.domain.Customer;

public interface CustomerDao extends Dao<Long, Customer> {
    Customer findByProjectId(Long id);
}
