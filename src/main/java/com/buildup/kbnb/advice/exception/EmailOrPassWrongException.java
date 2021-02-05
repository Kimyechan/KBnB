package com.buildup.kbnb.advice.exception;

public class EmailOrPassWrongException extends RuntimeException{
    private static final String MESSAGE = "email or password wrong";
    public EmailOrPassWrongException() {
        super(MESSAGE);
    }

    public EmailOrPassWrongException(String message) {
        super(message);
    }
}
