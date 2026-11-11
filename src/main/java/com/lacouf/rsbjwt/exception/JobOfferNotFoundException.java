package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class JobOfferNotFoundException extends APIException {
    public JobOfferNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
