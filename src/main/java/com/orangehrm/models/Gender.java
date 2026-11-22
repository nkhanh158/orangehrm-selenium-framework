package com.orangehrm.models;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    /**
     * Text of the radio button in the UI.
     */
    public String label() {
        return label;
    }
}
