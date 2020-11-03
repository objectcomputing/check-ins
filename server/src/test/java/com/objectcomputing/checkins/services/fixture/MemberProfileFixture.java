package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

import java.time.LocalDate;

public interface MemberProfileFixture extends RepositoryFixture {

    default MemberProfileEntity createADefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfileEntity("Mr. Bill", "Comedic Relief",
                null, "New York, New York", "billm@objectcomputing.com", "mr-bill-insperity",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows"));
    }

    default MemberProfileEntity createADefaultMemberProfileForPdl(MemberProfileEntity memberProfileEntity) {
        return getMemberProfileRepository().save(new MemberProfileEntity("Mr. Bill PDL", "Comedic Relief PDL",
                memberProfileEntity.getId(), "New York, New York", "billmpdl@objectcomputing.com", "mr-bill-insperity-pdl",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows"));
    }

    // this user is not connected to other users in the system
    default MemberProfileEntity createAnUnrelatedUser() {
        return getMemberProfileRepository().save(new MemberProfileEntity("Mr. Nobody", "Comedic Relief",
                null, "New York, New York", "nobody@objectcomputing.com", "mr-bill-insperity",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows"));
    }

}
