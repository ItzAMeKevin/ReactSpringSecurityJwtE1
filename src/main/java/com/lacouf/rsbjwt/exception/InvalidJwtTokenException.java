package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends APIException {
  public InvalidJwtTokenException(HttpStatus status, String message) {
    super(status, message);
  }
}
