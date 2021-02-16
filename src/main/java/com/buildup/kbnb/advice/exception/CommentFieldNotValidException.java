package com.buildup.kbnb.advice.exception;

public class CommentFieldNotValidException extends RuntimeException {
    private static final String MESSAGE = "check each score between 0 and 5, or check empty field";
    public CommentFieldNotValidException() {
        super(MESSAGE);
    }

    public CommentFieldNotValidException(String message) {
        super(message);
    }
}
