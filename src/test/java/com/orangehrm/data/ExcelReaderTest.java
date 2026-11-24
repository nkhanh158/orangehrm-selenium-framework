package com.orangehrm.data;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

@Feature("Framework")
@Story("Test data")
@Test(groups = "framework")
public class ExcelReaderTest {

    @Test
    public void shouldReadRowsKeyedByHeader() throws IOException {
        List<Map<String, String>> rows = ExcelReader.readSheet(workbook(), "employees");

        assertEquals(rows.size(), 2);
        assertEquals(rows.get(0).get("firstName"), "John");
        assertEquals(rows.get(0).get("employeeId"), "1001");
        assertEquals(rows.get(1).get("lastName"), "Nam");
    }

    @Test
    public void shouldKeepNumericCellsAsDisplayed() throws IOException {
        List<Map<String, String>> rows = ExcelReader.readSheet(workbook(), "employees");
        assertEquals(rows.get(0).get("employeeId"), "1001", "Numeric cell must not become 1001.0");
    }

    @Test
    public void shouldFailWhenSheetIsMissing() {
        assertThrows(IllegalArgumentException.class, () -> ExcelReader.readSheet(workbook(), "unknown"));
    }

    /**
     * Builds an in-memory workbook: header row, two records, one blank row in between.
     */
    private static InputStream workbook() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("employees");
            writeRow(sheet.createRow(0), "firstName", "lastName", "employeeId");
            Row first = sheet.createRow(1);
            first.createCell(0).setCellValue("John");
            first.createCell(1).setCellValue("Doe");
            first.createCell(2).setCellValue(1001);
            sheet.createRow(2);
            writeRow(sheet.createRow(3), "Le", "Nam", "");
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static void writeRow(Row row, String... values) {
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }
    }
}
