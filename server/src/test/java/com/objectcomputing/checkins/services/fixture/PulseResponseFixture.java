package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponse;

import java.time.LocalDate;

public interface PulseResponseFixture extends RepositoryFixture {
    default PulseResponse createADefaultPulseResponse(MemberProfile memberprofile) {
        return getPulseResponseRepository().save(new PulseResponse(1, 2, LocalDate.now(),
                memberprofile.getId(), "internalfeelings", "externalfeelings"));
    }
}
