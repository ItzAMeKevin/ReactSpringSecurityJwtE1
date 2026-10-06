package com.lacouf.rsbjwt.exception;

public class EmployerNotFoundException extends RuntimeException {

    private final Long employerId;

    public EmployerNotFoundException(Long employerId) {
        super("Employer not found with id: " + employerId);
        this.employerId = employerId;
    }

    public EmployerNotFoundException(String email) {
        super("Employer not found with email: " + email);
        this.employerId = null;
    }

    public Long getEmployerId() {
        return employerId;
    }
}