
package com.shivam.cloudlet_api.exceptions;

public class UserNotFoundException extends JwtAuthenticationException {
    public UserNotFoundException(String message) {
        super(message);
    }
}