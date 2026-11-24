package com.orangehrm.tests.pim;

import com.orangehrm.base.BaseTest;
import com.orangehrm.data.TestDataFactory;
import com.orangehrm.data.TestFiles;
import com.orangehrm.flows.EmployeeFlow;
import com.orangehrm.models.Employee;
import com.orangehrm.models.LoginDetails;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.pim.AddEmployeePage;
import com.orangehrm.pages.pim.AddEmployeePage.Field;
import com.orangehrm.pages.pim.EmployeeListPage;
import com.orangehrm.pages.pim.PersonalDetailsPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AddEmployeeTest extends BaseTest {

    private static final String AVATAR = "testdata/avatar.png";
    private static final int NAME_MAX_LENGTH = 30;

    @Test(groups = {"smoke", "regression"}, description = "PIM-01: Add employee with required fields only")
    public void shouldCreateEmployeeWithRequiredFieldsOnly() {
        Employee employee = TestDataFactory.randomEmployee();
        cleanupAfterTest(() -> new EmployeeFlow(driver).deleteEmployeeIfExists(employee.employeeId()));

        PersonalDetailsPage details = loginAsAdmin()
                .sidebar().goToPim()
                .clickAdd()
                .addEmployee(employee);

        assertEquals(details.toast().getMessage(), "Successfully Saved");
        assertTrue(details.isLoaded(), "Personal Details page should open after saving");
        assertEquals(details.getEmployee(), employee);
    }

    @Test(groups = "regression", description = "PIM-02: Add employee with a profile picture")
    public void shouldSaveUploadedProfilePicture() {
        Employee employee = TestDataFactory.randomEmployee();
        cleanupAfterTest(() -> new EmployeeFlow(driver).deleteEmployeeIfExists(employee.employeeId()));

        AddEmployeePage form = openAddEmployeeForm()
                .enterEmployee(employee)
                .uploadPicture(TestFiles.path(AVATAR));
        assertTrue(form.isPicturePreviewed(), "Selected picture should be previewed before saving");

        PersonalDetailsPage details = form.save();

        assertEquals(details.getEmployee(), employee);
        assertFalse(details.hasDefaultProfilePicture(), "Saved employee should show the uploaded picture");
    }

    @Test(groups = "regression", description = "PIM-03: Add employee together with login details")
    public void shouldLetNewEmployeeLogInWithCreatedCredentials() {
        Employee employee = TestDataFactory.randomEmployee();
        LoginDetails login = TestDataFactory.randomLoginDetails();
        // Deleting the employee also deletes its user. The test ends logged in as that user, who cannot delete.
        cleanupAfterTest(() -> {
            startNewAdminSession();
            new EmployeeFlow(driver).deleteEmployeeIfExists(employee.employeeId());
        });

        openAddEmployeeForm()
                .enterEmployee(employee)
                .enterLoginDetails(login)
                .save()
                .getEmployee();

        DashboardPage dashboard = new DashboardPage(driver).header().logout()
                .loginAs(login.username(), login.password());

        assertTrue(dashboard.isLoaded(), "New user should reach the Dashboard");
        assertEquals(dashboard.header().getUserName(), employee.fullName());
    }

    @Test(groups = "regression", description = "PIM-04: Required field and length validation")
    public void shouldShowValidationMessagesAndNotSave() {
        SoftAssert softly = new SoftAssert();
        AddEmployeePage form = openAddEmployeeForm().saveExpectingErrors();

        softly.assertEquals(form.getError(Field.FIRST_NAME), "Required", "First Name");
        softly.assertEquals(form.getError(Field.LAST_NAME), "Required", "Last Name");

        form.enter(Field.FIRST_NAME, "A".repeat(NAME_MAX_LENGTH + 1));
        softly.assertEquals(form.getError(Field.FIRST_NAME), "Should not exceed 30 characters", "First Name length");

        form.saveExpectingErrors();
        softly.assertTrue(form.isLoaded(), "Form with errors should not be saved");
        softly.assertAll();
    }

    @Test(groups = "regression", description = "PIM-05: Duplicate Employee Id")
    public void shouldRejectDuplicateEmployeeId() {
        Employee existing = TestDataFactory.randomEmployee();
        Employee duplicate = TestDataFactory.randomEmployee(existing.lastName()).withEmployeeId(existing.employeeId());
        EmployeeFlow flow = new EmployeeFlow(driver);
        cleanupAfterTest(() -> flow.deleteEmployeeIfExists(existing.employeeId()));

        loginAsAdmin();
        flow.createEmployee(existing);

        AddEmployeePage form = EmployeeListPage.open(driver).clickAdd()
                .enterEmployee(duplicate)
                .saveExpectingErrors();

        assertEquals(form.getError(Field.EMPLOYEE_ID), "Employee Id already exists");
        assertTrue(form.isLoaded(), "Form with a duplicate Employee Id should not be saved");
        assertEquals(EmployeeListPage.open(driver).searchByName(existing.lastName()).table().getRowCount(), 1,
                "Only the original employee should carry the marker");
    }

    private AddEmployeePage openAddEmployeeForm() {
        return loginAsAdmin().sidebar().goToPim().clickAdd();
    }
}
