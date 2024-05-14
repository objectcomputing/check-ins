package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.employee_hours.EmployeeHours;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursCSVHelper;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursServices;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.multipart.MultipartBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface EmployeeHoursFixture extends RepositoryFixture {
    default List<EmployeeHours> createEmployeeHours(){
        final EmployeeHoursServices employeeHoursServices = null;

        File file = new File("src/test/java/com/objectcomputing/checkins/services/employee_hours/test.csv");
        MultipartBody multipartBody = MultipartBody
                .builder()
                .addPart("file","test.csv",new MediaType("text/csv"),file)
                .build();
        List<EmployeeHours> employeeHoursList = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(file);
             employeeHoursList = EmployeeHoursCSVHelper.employeeHrsCsv(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getEmployeeHoursRepository().saveAll(employeeHoursList);
    }
}
