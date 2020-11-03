package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponse;

import java.time.LocalDate;

public interface PulseResponseFixture extends RepositoryFixture {
    default PulseResponse createADefaultPulseResponse(MemberProfileEntity memberprofile) {
        return getPulseResponseRepository().save(new PulseResponse(LocalDate.now(),LocalDate.now(),memberprofile.getId(),"internalfeelings","externalfeelings"));
    }
}
