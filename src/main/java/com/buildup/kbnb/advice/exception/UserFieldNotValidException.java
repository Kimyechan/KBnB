package com.buildup.kbnb.advice.exception;

public class UserFieldNotValidException extends RuntimeException {
    private static final String MESSAGE = "requesting field is not valid, delete a blank space or conform to the form";
    public UserFieldNotValidException() {
        super(MESSAGE);
    }

    public UserFieldNotValidException(String message) {
        super(message);
    }
}
