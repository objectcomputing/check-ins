package com.objectcomputing.checkins.services.employee_hours;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.memberprofile.memberphoto.MemberPhotoServiceImpl;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.*;

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class EmployeeHoursServicesImpl implements EmployeeHoursServices{

    private final CurrentUserServices currentUserServices;
    private final EmployeeHoursRepository employeeHourRepo;
    private static final Logger LOG = LoggerFactory.getLogger(MemberPhotoServiceImpl.class);



    public EmployeeHoursServicesImpl(CurrentUserServices currentUserServices,
                                     EmployeeHoursRepository employeeHourRepo) {
        this.currentUserServices = currentUserServices;
        this.employeeHourRepo = employeeHourRepo;
    }


    @Override
    public EmployeeHoursResponseDTO save(CompletedFileUpload file) {
        boolean isAdmin = currentUserServices.isAdmin();
        List<EmployeeHours> employeeHoursList;
        EmployeeHoursResponseDTO responseDTO = new EmployeeHoursResponseDTO();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        responseDTO.setRecordCountDeleted(employeeHourRepo.count());
        employeeHourRepo.deleteAll();
        try {
           employeeHoursList = EmployeeaHoursCSVHelper.employeeHrsCsv(file.getInputStream());
            employeeHourRepo.saveAll(employeeHoursList);
            Set<EmployeeHours> employeeHours = new HashSet<>(employeeHoursList);
            responseDTO.setRecordCountInserted(employeeHours.size());
            responseDTO.setEmployeehoursSet(employeeHours);
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
        }
        return  responseDTO;
    }


    @Override
    public EmployeeHours read(UUID id) {
        return employeeHourRepo.findById(id).orElse(null);
    }

    @Override
    public Set<EmployeeHours> findByFields(String employeeId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        Set<EmployeeHours> employeeHours = new HashSet<>(employeeHourRepo.findAll());

        if (employeeId != null) {
            validate(isAdmin || (currentUser != null
                    && currentUser.getEmployeeId() != null
                    && currentUser.getEmployeeId().equals(employeeId))).orElseThrow(() -> {
                throw new PermissionException("You are not authorized to perform this operation");
            });

            employeeHours.retainAll(employeeHourRepo.findByEmployeeId(employeeId));
        } else {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("You are not authorized to perform this operation");
            });
        }

        return employeeHours;
    }

}
