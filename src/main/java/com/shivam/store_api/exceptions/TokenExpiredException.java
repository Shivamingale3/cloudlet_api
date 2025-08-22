package com.shivam.store_api.exceptions;

public class TokenExpiredException extends JwtAuthenticationException {
    public TokenExpiredException(String message) {
        super(message);
    }
}