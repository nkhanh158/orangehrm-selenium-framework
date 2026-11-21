package com.orangehrm.models;

import java.util.Objects;

/**
 * Errors shown on the Login page after a failed attempt. A field is empty when that error is not shown.
 */
public record LoginErrors(String alert, String username, String password) {

    public LoginErrors {
        alert = Objects.requireNonNullElse(alert, "");
        username = Objects.requireNonNullElse(username, "");
        password = Objects.requireNonNullElse(password, "");
    }
}
