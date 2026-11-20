package com.orangehrm.data;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads an Excel sheet where the first row holds column names and every following row is one record.
 */
public final class ExcelReader {

    private ExcelReader() {
    }

    /**
     * Returns one map per data row, keyed by header name. Cell values are returned as displayed in Excel.
     * Blank rows are skipped.
     */
    public static List<Map<String, String>> readSheet(String classpathResource, String sheetName) {
        try (InputStream stream = ResourceLoader.open(classpathResource)) {
            return readSheet(stream, sheetName);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read Excel test data: " + classpathResource, e);
        }
    }

    static List<Map<String, String>> readSheet(InputStream stream, String sheetName) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(stream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found");
            }
            DataFormatter formatter = new DataFormatter();
            Row header = sheet.getRow(sheet.getFirstRowNum());
            List<Map<String, String>> records = new ArrayList<>();

            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, String> record = new LinkedHashMap<>();
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    String column = formatter.formatCellValue(header.getCell(c)).trim();
                    String value = formatter.formatCellValue(row.getCell(c)).trim();
                    record.put(column, value);
                }
                if (record.values().stream().anyMatch(value -> !value.isEmpty())) {
                    records.add(record);
                }
            }
            return records;
        }
    }
}
