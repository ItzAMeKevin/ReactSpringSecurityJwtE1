package com.lacouf.rsbjwt.security.exception;

import org.springframework.http.HttpStatus;

public class ApplicationNotFoundException extends APIException {
    public ApplicationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "applicationNotFound");
    }
}
