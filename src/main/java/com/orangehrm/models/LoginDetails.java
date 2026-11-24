package com.orangehrm.models;

import java.util.Objects;

/**
 * Credentials of the system user created together with an employee.
 */
public record LoginDetails(String username, String password) {

    public LoginDetails {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
    }

    /**
     * Masks the password, so that it never appears in logs or reports.
     */
    @Override
    public String toString() {
        return "LoginDetails[username=" + username + ", password=******]";
    }
}
