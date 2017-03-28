package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class TableNotInitializedException extends RuntimeException {
    public TableNotInitializedException() {
    }

    public TableNotInitializedException(String message) {
        super(message);
    }
}
