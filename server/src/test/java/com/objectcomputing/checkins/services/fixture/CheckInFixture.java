package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDateTime;

public interface CheckInFixture extends RepositoryFixture {

    default CheckIn createADefaultCheckIn(MemberProfile memberprofile, MemberProfile memberProfileForPDL) {
        return getCheckInRepository().save(new CheckIn(memberprofile.getId(), memberProfileForPDL.getId(), LocalDateTime.of(2000, 01, 01, 00, 00), false));
    }

    default CheckIn createACompletedCheckIn(MemberProfile memberprofile, MemberProfile memberProfileForPDL) {
        return getCheckInRepository().save(new CheckIn(memberprofile.getId(), memberProfileForPDL.getId(), LocalDateTime.of(2000, 01, 01, 00, 00), true));
    }
}
