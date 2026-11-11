package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class ManagerNotFoundException extends APIException {
    public ManagerNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ManagerNotFoundException(String message, Throwable cause) {
        super(HttpStatus.NOT_FOUND, message, cause);
    }
}
