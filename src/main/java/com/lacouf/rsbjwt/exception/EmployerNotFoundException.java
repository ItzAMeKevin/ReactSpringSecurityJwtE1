package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class EmployerNotFoundException extends APIException {

    private final Long employerId;

    public EmployerNotFoundException(Long employerId) {
        super(HttpStatus.NOT_FOUND, "errors.employerNotFound");
        this.employerId = employerId;
    }

    public EmployerNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, "errors.employerNotFound");
        this.employerId = null;
    }

    public Long getEmployerId() {
        return employerId;
    }
}