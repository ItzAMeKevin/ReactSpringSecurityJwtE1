package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class JobOfferUpdateException extends APIException {
    public JobOfferUpdateException() {
        super(HttpStatus.CONFLICT, "errors.jobOfferUpdateFailed");
    }
}
