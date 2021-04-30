package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.survey.Survey;
import com.objectcomputing.checkins.services.survey.SurveyRepository;
import com.objectcomputing.checkins.services.survey.SurveyService;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class SurveyServicesImpl implements SurveyService {

    private final SurveyRepository surveyResponseRepo;
    private final MemberProfileRepository memberRepo;

    public SurveyServicesImpl(SurveyRepository surveyResponseRepo,
                              MemberProfileRepository memberRepo) {
        this.surveyResponseRepo = surveyResponseRepo;
        this.memberRepo = memberRepo;
    }

    @Override
    public Survey save(Survey surveyResponse) {
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


    @Override
    public Survey read(@NotNull UUID id) {
        return surveyResponseRepo.findById(id).orElse(null);
    }

    @Override
    public Survey update(Survey surveyResponse) {
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
    public Set<Survey> findByFields(UUID createdBy, LocalDate dateFrom, LocalDate dateTo) {
        Set<Survey> surveyResponse = new HashSet<>();
        surveyResponseRepo.findAll().forEach(surveyResponse::add);
        if(createdBy!=null){
            surveyResponse.retainAll(surveyResponseRepo.findByCreatedBy(createdBy));
        } else if(dateFrom!=null && dateTo!=null) {
            surveyResponse.retainAll(surveyResponseRepo.findByCreatedOnBetween(dateFrom, dateTo));
        }
        return surveyResponse;
    }
}