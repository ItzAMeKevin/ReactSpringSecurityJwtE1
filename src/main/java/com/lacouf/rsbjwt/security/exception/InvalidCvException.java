package com.lacouf.rsbjwt.security.exception;

import org.springframework.http.HttpStatus;

public class InvalidCvException extends APIException {
    public InvalidCvException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
