package com.objectcomputing.checkins.services.survey;

import java.util.UUID;
import java.util.Set;

public interface SurveyService {

    Set<Survey> readAll();

    Survey save(Survey surveyResponse);

    Survey update(Survey surveyResponse);

    void delete(UUID id);

    Set<Survey> findByFields(String name, UUID createBy);

}