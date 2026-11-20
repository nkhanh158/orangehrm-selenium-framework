package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.models.Employee;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddEmployeePage extends AuthenticatedPage {

    private final By firstNameInput = By.name("firstName");
    private final By middleNameInput = By.name("middleName");
    private final By lastNameInput = By.name("lastName");
    private final By employeeIdInput = inputByLabel("Employee Id");
    private final By saveButton = By.cssSelector("button[type='submit']");

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    public PersonalDetailsPage addEmployee(Employee employee) {
        fillForm(employee);
        click(saveButton);
        return new PersonalDetailsPage(driver);
    }

    private void fillForm(Employee employee) {
        type(firstNameInput, employee.firstName());
        type(middleNameInput, employee.middleName());
        type(lastNameInput, employee.lastName());
        if (!employee.employeeId().isEmpty()) {
            type(employeeIdInput, employee.employeeId());
        }
    }
}
