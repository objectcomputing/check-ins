package com.objectcomputing.checkins.services.employee_hours;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.memberprofile.memberphoto.MemberPhotoServiceImpl;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.*;

@Singleton
public class EmployeeHoursServicesImpl implements EmployeeHoursServices{

    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final EmployeeHoursRepository employeehourRepo;
    private static final Logger LOG = LoggerFactory.getLogger(MemberPhotoServiceImpl.class);



    public EmployeeHoursServicesImpl(MemberProfileRepository memberRepo,
                                     CurrentUserServices currentUserServices,
                                     EmployeeHoursRepository employeehourRepo) {
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.employeehourRepo = employeehourRepo;
    }


    @Override
    public EmployeeHoursResponseDTO save(CompletedFileUpload file) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        List<EmployeeHours> employeeHoursList = new ArrayList<>();
        Set<EmployeeHours> employeeHours = new HashSet<>();
        EmployeeHoursResponseDTO responseDTO = new EmployeeHoursResponseDTO();
        validate(!isAdmin, "You are not authorized to perform this operation");
        responseDTO.setRecordCountDeleted(employeehourRepo.count());
        employeehourRepo.deleteAll();
        try {
           employeeHoursList = EmployeeaHoursCSVHelper.employeeHrsCsv(file.getInputStream());
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
    public EmployeeHours read(UUID id) {
        EmployeeHours result = employeehourRepo.findById(id).orElse(null);

        return result;
    }

    @Override
    public Set<EmployeeHours> findByFields(String employeeId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        Set<EmployeeHours> employeeHours = new HashSet<>();
        employeehourRepo.findAll().forEach(employeeHours::add);

        if(employeeId !=null) {
            validate((!isAdmin && currentUser!=null&& !currentUser.getEmployeeId().equals(employeeId)),
                       "You are not authorized to perform this operation");
            employeeHours.retainAll(employeehourRepo.findByEmployeeId(employeeId));
        } else {
            validate(!isAdmin, "You are not authorized to perform this operation");
        }


        return employeeHours;
    }


    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

}
