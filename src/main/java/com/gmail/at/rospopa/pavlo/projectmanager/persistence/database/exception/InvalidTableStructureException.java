package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class InvalidTableStructureException extends RuntimeException{

    public InvalidTableStructureException() {
    }

    public InvalidTableStructureException(String message) {
        super(message);
    }
}
