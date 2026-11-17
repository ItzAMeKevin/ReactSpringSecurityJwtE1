package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class InvalidApplicationStatusException extends APIException {
    public InvalidApplicationStatusException() {
        super(HttpStatus.BAD_REQUEST, "errors.invalidApplicationStatus");
    }
}
