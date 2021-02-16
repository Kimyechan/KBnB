package com.buildup.kbnb.advice.exception;

public class FieldNotValidException extends RuntimeException {
    private static final String MESSAGE = "requesting field is not valid, delete a blank space or conform to the form";
    public FieldNotValidException() {
        super(MESSAGE);
    }

    public FieldNotValidException(String message) {
        super(message);
    }
}
