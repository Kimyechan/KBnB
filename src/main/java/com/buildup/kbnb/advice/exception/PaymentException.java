package com.buildup.kbnb.advice.exception;

public class PaymentException extends RuntimeException {
    private static final String MESSAGE = "Payment Exception";
    public PaymentException() {
    }

    public PaymentException(String MESSAGE) {
        super(MESSAGE);
    }
}
