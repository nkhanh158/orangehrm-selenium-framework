package com.orangehrm.models;

import java.util.Objects;

/**
 * One data-driven login attempt and the errors it is expected to produce.
 */
public record LoginAttempt(String description, String username, String password, LoginErrors expected) {

    public LoginAttempt {
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(expected, "expected");
        username = Objects.requireNonNullElse(username, "");
        password = Objects.requireNonNullElse(password, "");
    }

    /**
     * Used by TestNG as the parameter label in reports.
     */
    @Override
    public String toString() {
        return description;
    }
}
