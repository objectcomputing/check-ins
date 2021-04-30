package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.services.survey.Survey;

import java.util.UUID;
import java.util.Set;
import java.time.LocalDate;

public interface SurveyService {

    Survey read(UUID id);

    Survey save(Survey surveyResponse);

    Survey update(Survey surveyResponse);

    Set<Survey> findByFields(UUID createBy, LocalDate dateFrom, LocalDate dateTo);
}