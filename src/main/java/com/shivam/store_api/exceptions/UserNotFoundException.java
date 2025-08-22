
package com.shivam.store_api.exceptions;

public class UserNotFoundException extends JwtAuthenticationException {
    public UserNotFoundException(String message) {
        super(message);
    }
}