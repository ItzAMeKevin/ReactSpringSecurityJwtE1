package com.lacouf.rsbjwt.exception;

public class NoValidatedCvException extends RuntimeException {
    public NoValidatedCvException() {
        super("No validated CV");
    }
}
