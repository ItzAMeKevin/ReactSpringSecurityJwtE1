package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class RegistrationConflictException extends APIException {
    public RegistrationConflictException(String errorKey) {
        super(HttpStatus.CONFLICT, errorKey);
    }
}
