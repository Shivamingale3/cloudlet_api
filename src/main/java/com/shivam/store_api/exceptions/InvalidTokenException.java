package com.shivam.store_api.exceptions;

public class InvalidTokenException extends JwtAuthenticationException {
    public InvalidTokenException(String message) {
        super(message);
    }
}