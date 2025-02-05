package com.objectcomputing.checkins.services.employee_hours;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.memberprofile.memberphoto.MemberPhotoServiceImpl;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class EmployeeHoursServicesImpl implements EmployeeHoursServices{

    private static final Logger LOG = LoggerFactory.getLogger(MemberPhotoServiceImpl.class);

    private final CurrentUserServices currentUserServices;
    private final EmployeeHoursRepository employeehourRepo;

    public EmployeeHoursServicesImpl(CurrentUserServices currentUserServices,
                                     EmployeeHoursRepository employeehourRepo) {
        this.currentUserServices = currentUserServices;
        this.employeehourRepo = employeehourRepo;
    }


    @Override
    @RequiredPermission(Permission.CAN_UPLOAD_HOURS)
    public EmployeeHoursResponseDTO save(CompletedFileUpload file) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        List<EmployeeHours> employeeHoursList = new ArrayList<>();
        Set<EmployeeHours> employeeHours = new HashSet<>();
        EmployeeHoursResponseDTO responseDTO = new EmployeeHoursResponseDTO();
        responseDTO.setRecordCountDeleted(employeehourRepo.count());
        employeehourRepo.deleteAll();
        try {
           employeeHoursList = EmployeeHoursCSVHelper.employeeHrsCsv(file.getInputStream());
            employeehourRepo.saveAll(employeeHoursList);
            for(EmployeeHours hours: employeeHoursList) {
                employeeHours.add(hours);
            }
            responseDTO.setRecordCountInserted((long) employeeHours.size());
            responseDTO.setEmployeehoursSet(employeeHours);
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
        }
        return  responseDTO;
    }


    @Override
    public Set<EmployeeHours> findByFields(String employeeId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canViewAll = currentUserServices.hasPermission(Permission.CAN_VIEW_ALL_UPLOADED_HOURS);

        Set<EmployeeHours> employeeHours = new HashSet<>();
        employeehourRepo.findAll().forEach(employeeHours::add);

        if(employeeId !=null) {
            validate((!canViewAll && currentUser!=null && !currentUser.getEmployeeId().equals(employeeId)),
                       NOT_AUTHORIZED_MSG);
            employeeHours.retainAll(employeehourRepo.findByEmployeeId(employeeId));
        } else {
            validate(!canViewAll, NOT_AUTHORIZED_MSG);
        }


        return employeeHours;
    }


    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

}
