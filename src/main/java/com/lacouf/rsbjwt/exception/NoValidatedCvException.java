package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class NoValidatedCvException extends APIException {
    public NoValidatedCvException() {
        super(HttpStatus.FORBIDDEN, "errors.noValidatedCv");
    }
}
