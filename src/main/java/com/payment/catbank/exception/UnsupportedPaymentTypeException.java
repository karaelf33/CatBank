package com.payment.catbank.exception;

public class UnsupportedPaymentTypeException extends RuntimeException {

    public UnsupportedPaymentTypeException(String message) {
        super(message);
    }
}
