package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class InvalidObjectTypeException extends RuntimeException {
    public InvalidObjectTypeException() {
    }

    public InvalidObjectTypeException(String message) {
        super(message);
    }
}
