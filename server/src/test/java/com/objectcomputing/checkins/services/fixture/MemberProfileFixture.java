package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Member;
import java.time.LocalDate;

public interface MemberProfileFixture extends RepositoryFixture {

    default MemberProfile createADefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee", LocalDate.now(),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null));
    }

    default MemberProfile createASecondDefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Jim", null, "Jimson",
                null, "Comedic Relief", null, "New York, New York",
                "jimj@objectcomputing.com", "mr-jim-employee", LocalDate.now(),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null, null));
    }


    default MemberProfile createADefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Bill PDL", null, "Johnson",
                null, "Comedic Relief PDL", memberProfile.getId(), "New York, New York",
                "billmpdl@objectcomputing.com", "mr-bill-employee-pdl",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows",
                memberProfile.getId(), null,null));
    }

    default MemberProfile createASecondDefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Sluggo PDL", null, "Simpson",
                null, "Bully Relief PDL", memberProfile.getId(), "New York, New York",
                "sluggopdl@objectcomputing.com", "sluggo-employee-pdl",
                LocalDate.now(), "is the bully in a clay figurine clown star of a parody of children's clay animation shows",
                memberProfile.getId(), null,null));
    }

    default MemberProfile createAThirdDefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Godzilla", null, "Godzilla",
                null, "local kaiju", memberProfile.getId(), "Tokyo, Japan",
                "godzilla@objectcomputing.com", "godzilla", LocalDate.now(),
                "is a destroyer of words",
                null, null, null));
    }

    // this user is not connected to other users in the system
    default MemberProfile createAnUnrelatedUser() {
        return getMemberProfileRepository().save(new MemberProfile("Nobody", null, " Really",
                null, "Comedic Relief", null, "New York, New York",
                "nobody@objectcomputing.com", "mr-bill-employee-unrelated",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null));
    }

    default MemberProfile createAPastTerminatedMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("past terminated", null, "user",
                null, "Bully Relief PDL", null, "New York, New York",
                "sluggopdl@objectcomputing.com", "sluggo-employee-pdl-past-terminated",
                LocalDate.now(), "is the bully in a clay figurine clown star of a parody of children's clay animation shows",
                null, LocalDate.now().minusDays(7),null));
    }

    default MemberProfile createAFutureTerminatedMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("past terminated", null, "user",
                null, "Bully Relief PDL", null, "New York, New York",
                "sluggopdl@objectcomputing.com", "sluggo-employee-pdl-future terminated",
                LocalDate.now(), "is the bully in a clay figurine clown star of a parody of children's clay animation shows",
                null, LocalDate.now().plusDays(7),null));

    }

    default MemberProfile createADefaultMemberProfileWithBirthDay() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee-birthday", LocalDate.now(),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null, LocalDate.now()));
    }


}

