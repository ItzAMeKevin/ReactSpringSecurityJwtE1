package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class InvalidStateTransitionException extends APIException {
    public InvalidStateTransitionException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
