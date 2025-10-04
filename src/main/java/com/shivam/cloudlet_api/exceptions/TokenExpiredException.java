package com.shivam.cloudlet_api.exceptions;

public class TokenExpiredException extends JwtAuthenticationException {
    public TokenExpiredException(String message) {
        super(message);
    }
}