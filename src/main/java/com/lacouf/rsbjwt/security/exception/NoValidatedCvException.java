package com.lacouf.rsbjwt.security.exception;

public class NoValidatedCvException extends RuntimeException {
    public NoValidatedCvException() {
        super("No validated CV");
    }
}
