package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class DatabaseNotInitializedException extends RuntimeException {
    public DatabaseNotInitializedException() {
    }

    public DatabaseNotInitializedException(String message) {
        super(message);
    }
}
