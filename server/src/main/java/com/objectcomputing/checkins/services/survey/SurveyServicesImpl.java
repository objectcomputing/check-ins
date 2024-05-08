package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class SurveyServicesImpl implements SurveyService {

    private final SurveyRepository surveyResponseRepo;
    private final MemberProfileRepository memberRepo;
    private final PermissionsValidation permissionsValidation;
    private final CurrentUserServices currentUserServices;

    public SurveyServicesImpl(SurveyRepository surveyResponseRepo,
                              MemberProfileRepository memberRepo,
                              PermissionsValidation permissionsValidation,
                              CurrentUserServices currentUserServices) {
        this.surveyResponseRepo = surveyResponseRepo;
        this.memberRepo = memberRepo;
        this.permissionsValidation = permissionsValidation;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Survey save(Survey surveyResponse) {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        Survey surveyResponseRet = null;
        if(surveyResponse!=null){
            final UUID memberId = surveyResponse.getCreatedBy();
            LocalDate surSubDate = surveyResponse.getCreatedOn();
            if(surveyResponse.getId()!=null){
                throw new BadArgException(String.format("Found unexpected id for survey %s", surveyResponse.getId()));
            } else if(!memberRepo.findById(memberId).isPresent()){
                throw new BadArgException(String.format("Member %s doesn't exists", memberId));
            } else if(surSubDate.isBefore(LocalDate.EPOCH) || surSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException(String.format("Invalid date for survey submission date %s",memberId));
            }
            surveyResponseRet = surveyResponseRepo.save(surveyResponse);
        }
        return surveyResponseRet ;
    }

    public Set<Survey> readAll() {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        return new HashSet<>(surveyResponseRepo.findAll());
    }

    @Override
    public Survey update(Survey surveyResponse) {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        Survey surveyResponseRet = null;
        if(surveyResponse!=null){
            final UUID id = surveyResponse.getId();
            final UUID memberId = surveyResponse.getCreatedBy();
            LocalDate surSubDate = surveyResponse.getCreatedOn();
            if(id==null||!surveyResponseRepo.findById(id).isPresent()){
                throw new BadArgException(String.format("Unable to find survey record with id %s", surveyResponse.getId()));
            }else if(!memberRepo.findById(memberId).isPresent()){
                throw new BadArgException(String.format("Member %s doesn't exist", memberId));
            } else if(memberId==null) {
                throw new BadArgException(String.format("Invalid survey %s", surveyResponse));
            } else if(surSubDate.isBefore(LocalDate.EPOCH) || surSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException(String.format("Invalid date for survey submission date %s",memberId));
            }

            surveyResponseRet = surveyResponseRepo.update(surveyResponse);
        }
        return surveyResponseRet;
    }

    @Override
    public void delete(@NotNull UUID id) {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        surveyResponseRepo.deleteById(id);
    }

    @Override
    public Set<Survey> findByFields(String name, UUID createdBy) {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");

        Set<Survey> surveyResponse = new HashSet<>();
        if(name != null && createdBy != null) {
            surveyResponse = surveyResponseRepo.findByNameAndCreatedBy(name, createdBy);
        } else if  (name != null) {
            surveyResponse = surveyResponseRepo.findByName(name);
        } else if (createdBy != null){
            surveyResponse = surveyResponseRepo.findByCreatedBy(createdBy);
        }

        return surveyResponse;
    }
}