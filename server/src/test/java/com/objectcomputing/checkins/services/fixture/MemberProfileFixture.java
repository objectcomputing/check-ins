package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;

public interface MemberProfileFixture extends RepositoryFixture {

    default MemberProfile createADefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee", LocalDate.now(),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null));
    }

    default MemberProfile createASecondMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Slim", null, "Jim",
                null, "Office Opossum", null, "New York, New York",
                "slimjim@objectcomputing.com", "slim-jim-employee", LocalDate.now(),
                "A Virginia opossum, one of North America's only marsupials",
                null, null,null));
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

    default MemberProfile createADefaultSupervisor() {
        return getMemberProfileRepository().save(new MemberProfile("dude", null, "bro",
                null, "Supervisor Man", null, "New York, New York",
                "dubebro@objectcomputing.com", "dude-bro-supervisor",
                LocalDate.now(), "is such like a bro dude, you know?",
                null, null,null));
    }
    default MemberProfile createASupervisedAndPDLUser(MemberProfile supervisorProfile, MemberProfile pdlProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Charizard", null, "Char",
                null, "Local fire hazard", pdlProfile.getId(), "New York, New York",
                "charizard@objectcomputing.com", "local-kaiju",
                LocalDate.now(), "Needs a lot of supervision due to building being ultra flammable",
                supervisorProfile.getId(), null,null));
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
