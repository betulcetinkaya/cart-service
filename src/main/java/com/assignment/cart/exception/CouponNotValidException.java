package com.assignment.cart.exception;

public final class CouponNotValidException extends RuntimeException {

    public CouponNotValidException(final String message) {
        super(message);
    }
}
