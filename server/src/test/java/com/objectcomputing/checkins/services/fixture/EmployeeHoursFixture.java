package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.employee_hours.EmployeeHours;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursCSVHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface EmployeeHoursFixture extends RepositoryFixture {

    default List<EmployeeHours> createEmployeeHours() throws IOException {
        File file = new File("src/test/java/com/objectcomputing/checkins/services/employee_hours/test.csv");
        try(InputStream inputStream = new FileInputStream(file)) {
            return getEmployeeHoursRepository().saveAll(EmployeeHoursCSVHelper.employeeHrsCsv(inputStream));
        }
    }
}
