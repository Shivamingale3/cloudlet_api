package com.shivam.store_api.wrappers;

import com.shivam.store_api.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class UserRequestWrapper extends HttpServletRequestWrapper {
    private User user;

    public UserRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
