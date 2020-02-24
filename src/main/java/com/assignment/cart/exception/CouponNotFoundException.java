package com.assignment.cart.exception;

public final class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(final String message) {
        super(message);
    }

}
