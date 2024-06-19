package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.opportunities.Opportunities;

import java.time.LocalDate;

public interface OpportunitiesFixture extends RepositoryFixture {
    default Opportunities createADefaultOpportunities(MemberProfile memberprofile) {
        return getOpportunitiesRepository().save(new Opportunities("Name", "Description","https://objectcomputing.com/jobs", LocalDate.now(), LocalDate.now(),
                memberprofile.getId(), false));
    }
}
