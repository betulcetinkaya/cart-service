package com.assignment.cart.exception;

public final class ShoppingCartNotFoundException extends RuntimeException {

    public ShoppingCartNotFoundException(final String message) {
        super(message);
    }

}
