package com.assignment.cart.exception;

/**
 * @author betulc
 */
public final class ServiceExecutionException extends RuntimeException {
    public ServiceExecutionException() {
    }

    public ServiceExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ServiceExecutionException(final String message) {
        super(message);
    }

    public ServiceExecutionException(final Throwable cause) {
        super(cause);
    }
}