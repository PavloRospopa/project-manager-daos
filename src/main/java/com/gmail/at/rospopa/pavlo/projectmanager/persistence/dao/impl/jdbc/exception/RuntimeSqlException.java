package com.gmail.at.rospopa.pavlo.projectmanager.persistence.dao.impl.jdbc.exception;

public class RuntimeSqlException extends RuntimeException {
    public RuntimeSqlException() {
    }

    public RuntimeSqlException(String message) {
        super(message);
    }
}
