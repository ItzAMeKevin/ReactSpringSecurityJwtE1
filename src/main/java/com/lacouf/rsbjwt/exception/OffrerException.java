package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class OffrerException extends APIException {
    public OffrerException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
