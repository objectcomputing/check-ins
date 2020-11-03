package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

import java.time.LocalDateTime;

public interface CheckInFixture extends RepositoryFixture {

    default CheckIn createADefaultCheckIn(MemberProfileEntity memberprofile, MemberProfileEntity memberProfileEntityForPDL) {
        return getCheckInRepository().save(new CheckIn(memberprofile.getId(), memberProfileEntityForPDL.getId(), LocalDateTime.of(2000, 01, 01, 00, 00),false));
    }

    default CheckIn createACompletedCheckIn(MemberProfileEntity memberprofile, MemberProfileEntity memberProfileEntityForPDL) {
        return getCheckInRepository().save(new CheckIn(memberprofile.getId(), memberProfileEntityForPDL.getId(), LocalDateTime.of(2000, 01, 01, 00, 00),true));
    }
}
