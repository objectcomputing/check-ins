package com.objectcomputing.checkins.services.employee_hours;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeHoursCSVHelper {

    private EmployeeHoursCSVHelper() {
    }

    public static List<EmployeeHours> employeeHrsCsv(InputStream inputStream) throws IOException {
        try (BOMInputStream bomInputStream = BOMInputStream.builder().setInputStream(inputStream).setInclude(false).get();
             InputStreamReader input = new InputStreamReader(bomInputStream)) {
            List<EmployeeHours> employeeHoursList = new ArrayList<>();
            CSVParser csvParser = CSVFormat.RFC4180
                    .builder()
                    .setHeader().setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setNullString("")
                    .build()
                    .parse(input);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            for (CSVRecord csvRecord : csvParser) {
                EmployeeHours employeeHours = new EmployeeHours(csvRecord.get("employeeId"),
                        Float.parseFloat(csvRecord.get("contributionHours")),
                        Float.parseFloat(csvRecord.get("billableHours")),
                        Float.parseFloat(csvRecord.get("ptoHours")), LocalDate.now(), Float.parseFloat(csvRecord.get("targetHours")),
                        LocalDate.parse(csvRecord.get("asOfDate"), formatter));
                employeeHoursList.add(employeeHours);
            }
            return employeeHoursList;
        }
    }
}


