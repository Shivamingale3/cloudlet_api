package com.shivam.cloudlet_api.exceptions;

public class InvalidTokenException extends JwtAuthenticationException {
    public InvalidTokenException(String message) {
        super(message);
    }
}