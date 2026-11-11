package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class ApplicationStateException extends APIException {
    public ApplicationStateException() {
        super(HttpStatus.CONFLICT, "errors.applicationNotPending");
    }
}
