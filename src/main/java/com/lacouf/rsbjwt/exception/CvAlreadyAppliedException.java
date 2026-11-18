package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class CvAlreadyAppliedException extends APIException {
    public CvAlreadyAppliedException() {
        super(HttpStatus.CONFLICT, "errors.cvAlreadyApplied");
    }
}
