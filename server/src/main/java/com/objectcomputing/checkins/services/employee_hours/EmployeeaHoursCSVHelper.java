package com.objectcomputing.checkins.services.employee_hours;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeaHoursCSVHelper {

    public static List<EmployeeHours> employeeHrsCsv(InputStream inputStream) {
        try {
            List<EmployeeHours> employeeHoursList = new ArrayList<>();

            InputStreamReader input = new InputStreamReader(new BOMInputStream(inputStream,false));
            CSVParser csvParser = CSVFormat.RFC4180.withFirstRecordAsHeader().withIgnoreSurroundingSpaces().withNullString("").parse(input);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            for (CSVRecord csvRecord : csvParser) {
                EmployeeHours employeeHours = new EmployeeHours(csvRecord.get("employeeId"),
                        Float.parseFloat(csvRecord.get("contributionHours")),
                        Float.parseFloat(csvRecord.get("billableHours")),
                         Float.parseFloat(csvRecord.get("ptoHours")), LocalDate.now(),Float.parseFloat(csvRecord.get("targetHours")),
                        LocalDate.parse(csvRecord.get("asOfDate"), formatter));
                employeeHoursList.add(employeeHours);
            }
            return employeeHoursList;
        } catch (IOException e) {
            throw new RuntimeException("unable to read csv file:"+e.getMessage());
        }
    }
}


