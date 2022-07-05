package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class SurveyServicesImpl implements SurveyService {

    private final SurveyRepository surveyResponseRepo;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final PermissionsValidation permissionsValidation;
    private final CurrentUserServices currentUserServices;

    public SurveyServicesImpl(SurveyRepository surveyResponseRepo,
                              MemberProfileRetrievalServices memberProfileRetrievalServices,
                              PermissionsValidation permissionsValidation,
                              CurrentUserServices currentUserServices) {
        this.surveyResponseRepo = surveyResponseRepo;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
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
                throw new BadArgException("Found unexpected id for survey %s", surveyResponse.getId());
            } else if(memberProfileRetrievalServices.getById(memberId).isEmpty()){
                throw new BadArgException("Member %s doesn't exists", memberId);
            } else if(surSubDate.isBefore(LocalDate.EPOCH) || surSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException("Invalid date for survey submission date %s",memberId);
            }
            surveyResponseRet = surveyResponseRepo.save(surveyResponse);
        }
        return surveyResponseRet ;
    }

    public Set<Survey> readAll() {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        return surveyResponseRepo.findAll();
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
                throw new BadArgException("Unable to find survey record with id %s", surveyResponse.getId());
            }else if(memberProfileRetrievalServices.getById(memberId).isEmpty()){
                throw new BadArgException("Member %s doesn't exist", memberId);
            } else if(memberId==null) {
                throw new BadArgException("Invalid survey %s", surveyResponse);
            } else if(surSubDate.isBefore(LocalDate.EPOCH) || surSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException("Invalid date for survey submission date %s",memberId);
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
        surveyResponseRepo.findAll().forEach(surveyResponse::add);
        if(name!=null){
            surveyResponse.addAll(surveyResponseRepo.findByName(name));
        } else if(createdBy!=null){
            surveyResponse.retainAll(surveyResponseRepo.findByCreatedBy(createdBy));
        }
        return surveyResponse;
    }
}