package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class CvException extends APIException {
    public CvException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
