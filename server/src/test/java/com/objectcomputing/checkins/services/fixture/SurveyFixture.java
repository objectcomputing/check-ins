package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.survey.Survey;

import java.time.LocalDate;

public interface SurveyFixture extends RepositoryFixture {
    default Survey createADefaultSurvey(MemberProfile memberprofile) {
        return getSurveyRepository().save(new Survey(LocalDate.now(),
                memberprofile.getId(), "Name", "Description"));
    }
}
