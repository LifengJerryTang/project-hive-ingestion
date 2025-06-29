package com.projecthive.summarization.exceptions;

public class DaoDataAccessException extends RuntimeException {
    public DaoDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoDataAccessException(String message) {
        super(message);
    }
}

