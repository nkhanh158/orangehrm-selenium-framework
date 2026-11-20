package com.orangehrm.models;

import java.util.Objects;

/**
 * Employee data used by PIM tests. {@code middleName} may be empty; {@code employeeId} may be empty
 * to let OrangeHRM generate one.
 */
public record Employee(String firstName, String middleName, String lastName, String employeeId) {

    public Employee {
        Objects.requireNonNull(firstName, "firstName");
        Objects.requireNonNull(lastName, "lastName");
        middleName = Objects.requireNonNullElse(middleName, "");
        employeeId = Objects.requireNonNullElse(employeeId, "");
    }

    /**
     * Name as OrangeHRM shows it in tables and autocomplete suggestions: "First Middle Last".
     */
    public String fullName() {
        return middleName.isEmpty()
                ? firstName + " " + lastName
                : firstName + " " + middleName + " " + lastName;
    }

    public Employee withEmployeeId(String newEmployeeId) {
        return new Employee(firstName, middleName, lastName, newEmployeeId);
    }
}
