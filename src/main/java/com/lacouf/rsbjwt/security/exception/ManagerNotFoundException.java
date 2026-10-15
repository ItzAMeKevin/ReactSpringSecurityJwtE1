package com.lacouf.rsbjwt.security.exception;

public class ManagerNotFoundException extends RuntimeException {
    public ManagerNotFoundException(String message) {
        super(message);
    }

    public ManagerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
