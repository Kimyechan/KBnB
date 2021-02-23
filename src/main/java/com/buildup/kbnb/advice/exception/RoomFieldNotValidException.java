package com.buildup.kbnb.advice.exception;

public class RoomFieldNotValidException extends RuntimeException {
    private static final String MESSAGE = "requesting field is not valid, delete a blank space or conform to the form";
    public RoomFieldNotValidException() {
        super(MESSAGE);
    }

    public RoomFieldNotValidException(String message) {
        super(message);
    }
}
