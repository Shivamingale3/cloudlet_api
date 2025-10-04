package com.shivam.cloudlet_api.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final Throwable rootCause;

    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
        this.rootCause = null;
    }

    public CustomException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.message = message;
        this.rootCause = cause;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Throwable getRootCause() {
        return rootCause;
    }
}