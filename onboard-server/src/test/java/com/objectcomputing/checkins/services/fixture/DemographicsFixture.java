package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.demographics.Demographics;

import java.util.UUID;

public interface DemographicsFixture extends RepositoryFixture {
    default Demographics createDefaultDemographics(UUID memberId) {
        return getDemographicsRepository().save(new Demographics(memberId, "male", "BS", 6, false, false, null, "none"));
    }
}
