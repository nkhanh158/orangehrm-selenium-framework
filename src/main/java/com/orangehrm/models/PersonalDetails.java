package com.orangehrm.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Values of the Personal Details form. Empty text fields are empty strings; {@code dateOfBirth} and
 * {@code gender} are {@code null} when not set.
 */
public record PersonalDetails(Employee employee, String otherId, String nationality, String maritalStatus,
                              LocalDate dateOfBirth, Gender gender) {

    public PersonalDetails {
        Objects.requireNonNull(employee, "employee");
        otherId = Objects.requireNonNullElse(otherId, "");
        nationality = Objects.requireNonNullElse(nationality, "");
        maritalStatus = Objects.requireNonNullElse(maritalStatus, "");
    }

    public PersonalDetails withMiddleName(String middleName) {
        return new PersonalDetails(employee.withMiddleName(middleName), otherId, nationality, maritalStatus,
                dateOfBirth, gender);
    }

    public PersonalDetails withDateOfBirth(LocalDate newDateOfBirth) {
        return new PersonalDetails(employee, otherId, nationality, maritalStatus, newDateOfBirth, gender);
    }

    public PersonalDetails withGender(Gender newGender) {
        return new PersonalDetails(employee, otherId, nationality, maritalStatus, dateOfBirth, newGender);
    }
}
