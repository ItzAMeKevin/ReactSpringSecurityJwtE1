package com.lacouf.rsbjwt.exception;

import org.springframework.http.HttpStatus;

public class NotificationNotFound extends APIException {
    public NotificationNotFound() {
        super(HttpStatus.NOT_FOUND, "errors.notificationNotFound");
    }
}
