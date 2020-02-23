package com.assignment.cart.exception;

public final class ShoppingCartNotFoundException extends RuntimeException {

    private String id;

    public ShoppingCartNotFoundException() {
    }

    public ShoppingCartNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ShoppingCartNotFoundException(final String message) {
        super(message);
    }

    public ShoppingCartNotFoundException(String id, final String message) {
        super(message);
        this.id = id;
    }

    public ShoppingCartNotFoundException(final Throwable cause) {
        super(cause);
    }
}
