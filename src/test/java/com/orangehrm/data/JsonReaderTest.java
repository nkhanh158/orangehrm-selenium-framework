package com.orangehrm.data;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.orangehrm.models.Employee;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

@Test(groups = "framework")
public class JsonReaderTest {

    @Test
    public void shouldMapJsonArrayToRecords() throws IOException {
        String json = """
                [
                  {"firstName": "John", "lastName": "Doe", "employeeId": "0001"},
                  {"firstName": "Lê", "middleName": "Văn", "lastName": "Nam"}
                ]
                """;

        List<Employee> employees = JsonReader.readList(stream(json), Employee.class);

        assertEquals(employees.size(), 2);
        assertEquals(employees.get(0).fullName(), "John Doe");
        assertEquals(employees.get(1).fullName(), "Lê Văn Nam");
        assertEquals(employees.get(1).employeeId(), "");
    }

    @Test
    public void shouldFailOnMisspelledRequiredField() {
        String json = """
                [{"firstName": "John", "lastname": "Doe"}]
                """;
        assertThrows(JsonMappingException.class, () -> JsonReader.readList(stream(json), Employee.class));
    }

    @Test
    public void shouldFailOnUnknownOptionalField() {
        String json = """
                [{"firstName": "John", "lastName": "Doe", "middlename": "Paul"}]
                """;
        assertThrows(UnrecognizedPropertyException.class, () -> JsonReader.readList(stream(json), Employee.class));
    }

    @Test
    public void shouldFailWhenFileIsMissing() {
        assertThrows(IllegalStateException.class, () -> JsonReader.readList("testdata/missing.json", Employee.class));
    }

    private static InputStream stream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
