package com.assignment.cart.exception;

public final class CouponNotFoundException extends RuntimeException {

    private String id;

    public CouponNotFoundException() {
    }

    public CouponNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CouponNotFoundException(final String message) {
        super(message);
    }

    public CouponNotFoundException(String id, final String message) {
        super(message);
        this.id = id;
    }

    public CouponNotFoundException(final Throwable cause) {
        super(cause);
    }
}
