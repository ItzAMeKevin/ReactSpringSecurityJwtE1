package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public abstract class APIException extends RuntimeException {
	private final HttpStatus status;
	private final String errorKey;

	protected APIException(HttpStatus status, String errorKey) {
		super(errorKey);
		this.status = status;
		this.errorKey = errorKey;
	}

	protected APIException(HttpStatus status, String errorKey, Throwable cause) {
		super(errorKey, cause);
		this.status = status;
		this.errorKey = errorKey;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getErrorKey() {
		return errorKey;
	}
}
