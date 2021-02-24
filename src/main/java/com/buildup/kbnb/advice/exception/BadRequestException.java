package com.buildup.kbnb.advice.exception;

public class BadRequestException extends RuntimeException {
    private static final String MESSAGE = "Not valid request info";

    public BadRequestException() {
        super(MESSAGE);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
