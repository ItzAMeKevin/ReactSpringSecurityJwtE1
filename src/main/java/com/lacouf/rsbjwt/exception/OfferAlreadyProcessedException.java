package com.lacouf.rsbjwt.exception;

public class OfferAlreadyProcessedException extends RuntimeException {
  public OfferAlreadyProcessedException(String message) {
    super(message);
  }
}
