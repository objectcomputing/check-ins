package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class SurveyServicesImpl implements SurveyService {

    private final SurveyRepository surveyResponseRepo;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;

    public SurveyServicesImpl(SurveyRepository surveyResponseRepo,
                              MemberProfileRetrievalServices memberProfileRetrievalServices,
                              CurrentUserServices currentUserServices) {
        this.surveyResponseRepo = surveyResponseRepo;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Survey save(Survey surveyResponse) {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        Survey surveyResponseRet = null;
        if (surveyResponse != null) {
            final UUID memberId = surveyResponse.getCreatedBy();
            LocalDate surSubDate = surveyResponse.getCreatedOn();

            validate(surveyResponse.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id for survey %s", surveyResponse.getId());
            });
            validate(memberProfileRetrievalServices.existsById(memberId)).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", memberId);
            });
            validate(surSubDate.isAfter(LocalDate.EPOCH) && surSubDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for survey submission date %s",memberId);
            });

            surveyResponseRet = surveyResponseRepo.save(surveyResponse);
        }
        return surveyResponseRet;
    }

    public Set<Survey> readAll() {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });
        return surveyResponseRepo.findAll();
    }

    @Override
    public Survey update(Survey surveyResponse) {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        Survey surveyResponseRet = null;
        if (surveyResponse != null) {
            final UUID id = surveyResponse.getId();
            final UUID memberId = surveyResponse.getCreatedBy();
            LocalDate surSubDate = surveyResponse.getCreatedOn();

            validate(id != null && surveyResponseRepo.findById(id).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Unable to find survey record with id %s", surveyResponse.getId());
            });
            validate(memberProfileRetrievalServices.existsById(memberId)).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", memberId);
            });
            validate(surSubDate.isAfter(LocalDate.EPOCH) && surSubDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for survey submission date %s", memberId);
            });

            surveyResponseRet = surveyResponseRepo.update(surveyResponse);
        }
        return surveyResponseRet;
    }

    @Override
    public void delete(@NotNull UUID id) {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });
        surveyResponseRepo.deleteById(id);
    }

    @Override
    public Set<Survey> findByFields(String name, UUID createdBy) {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });
        Set<Survey> surveyResponse = new HashSet<>(surveyResponseRepo.findAll());
        if (name != null) {
            surveyResponse.addAll(surveyResponseRepo.findByName(name));
        } else if (createdBy != null) {
            surveyResponse.retainAll(surveyResponseRepo.findByCreatedBy(createdBy));
        }
        return surveyResponse;
    }
}