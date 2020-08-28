package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;

public interface MemberProfileFixture extends RepositoryFixture {

    default MemberProfile createADefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Mr. Bill", "Comedic Relief",
                null, "New York, New York", "billm@objectcomputing.com", "mr-bill-insperity",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows"));
    }

    default MemberProfile createADefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Mr. Bill PDL", "Comedic Relief PDL",
                memberProfile.getUuid(), "New York, New York", "billmpdl@objectcomputing.com", "mr-bill-insperity-pdl",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows"));
    }
}
