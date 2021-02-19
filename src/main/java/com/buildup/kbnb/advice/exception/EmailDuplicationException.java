package com.buildup.kbnb.advice.exception;

public class EmailDuplicationException extends RuntimeException {
    private static final String MESSAGE =  "Email address already in use.";
    public EmailDuplicationException() {
        super(MESSAGE);
    }

    public EmailDuplicationException(String message) {
        super(message);
    }
}
