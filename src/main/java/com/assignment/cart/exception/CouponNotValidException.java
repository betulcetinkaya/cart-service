package com.assignment.cart.exception;

public final class CouponNotValidException extends RuntimeException {

    private String id;

    public CouponNotValidException() {
    }

    public CouponNotValidException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CouponNotValidException(final String message) {
        super(message);
    }

    public CouponNotValidException(String id, final String message) {
        super(message);
        this.id = id;
    }

    public CouponNotValidException(final Throwable cause) {
        super(cause);
    }
}
