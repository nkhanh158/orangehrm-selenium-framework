package com.orangehrm.data;

import com.orangehrm.models.Employee;
import com.orangehrm.models.LoginDetails;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Framework")
@Story("Test data")
@Test(groups = "framework")
public class TestDataFactoryTest {

    @Test
    public void shouldGenerateSuffixWithExpectedFormat() {
        assertTrue(TestDataFactory.uniqueSuffix().matches("Auto_\\d{8}_\\d{6}_[0-9a-f]{4}"));
    }

    @Test
    public void shouldGenerateDistinctEmployeesInSameSecond() {
        Set<String> lastNames = new HashSet<>();
        Set<String> ids = new HashSet<>();
        IntStream.range(0, 50).forEach(i -> {
            Employee employee = TestDataFactory.randomEmployee();
            lastNames.add(employee.lastName());
            ids.add(employee.employeeId());
        });
        assertTrue(lastNames.size() >= 49, "Unique markers collided: " + lastNames.size() + " of 50");
        assertTrue(ids.size() >= 49, "Employee ids collided: " + ids.size() + " of 50");
    }

    @Test
    public void shouldRespectOrangeHrmFieldLimits() {
        Employee employee = TestDataFactory.randomEmployeeWithMiddleName();
        assertEquals(employee.employeeId().length(), 10);
        assertTrue(employee.lastName().length() <= 30);
        assertFalse(employee.middleName().isEmpty());
    }

    @Test
    public void shouldGenerateLoginDetailsThatMeetPasswordRules() {
        LoginDetails login = TestDataFactory.randomLoginDetails();
        assertTrue(login.username().matches("auto_\\d{8}_\\d{6}_[0-9a-f]{4}"), login.username());
        assertTrue(login.password().matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}"),
                login.password());
    }

    @Test
    public void shouldBuildFullNameWithAndWithoutMiddleName() {
        assertEquals(new Employee("John", null, "Doe", null).fullName(), "John Doe");
        assertEquals(new Employee("John", "Paul", "Doe", "").fullName(), "John Paul Doe");
    }
}
