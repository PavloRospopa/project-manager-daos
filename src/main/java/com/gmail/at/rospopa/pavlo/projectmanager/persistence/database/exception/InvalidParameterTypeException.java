package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class InvalidParameterTypeException extends RuntimeException {
    public InvalidParameterTypeException() {
    }

    public InvalidParameterTypeException(String message) {
        super(message);
    }
}
