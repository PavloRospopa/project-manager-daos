package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class InvalidDatabaseStructureException extends RuntimeException {
    public InvalidDatabaseStructureException() {
    }

    public InvalidDatabaseStructureException(String message) {
        super(message);
    }
}
