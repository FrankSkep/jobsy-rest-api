package com.fran.jobsy.app.exception.custom;

public class InvalidUserRegistrationException extends RuntimeException {
    public InvalidUserRegistrationException(String message) {
        super(message);
    }
}
