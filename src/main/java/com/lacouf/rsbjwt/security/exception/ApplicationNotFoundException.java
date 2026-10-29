package com.lacouf.rsbjwt.security.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException() {
        super("Candidature introuvable");
    }
}
