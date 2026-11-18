package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class OfferAlreadyProcessedException extends APIException {
  public OfferAlreadyProcessedException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
