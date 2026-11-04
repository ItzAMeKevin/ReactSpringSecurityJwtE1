package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class ApplicationNotFoundException extends APIException {
    public ApplicationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "applicationNotFound");
    }
}
