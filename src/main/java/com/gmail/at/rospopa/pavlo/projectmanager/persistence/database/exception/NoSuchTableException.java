package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception;

public class NoSuchTableException extends RuntimeException {
    public NoSuchTableException() {
    }

    public NoSuchTableException(String message) {
        super(message);
    }
}
