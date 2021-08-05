package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;

public interface MemberProfileFixture extends RepositoryFixture {

    default MemberProfile createADefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee", LocalDate.now(),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null,null,null));
    }

    default MemberProfile createASecondDefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Slim", null, "Jim",
                null, "Office Opossum", null, "New York, New York",
                "slimjim@objectcomputing.com", "slim-jim-employee", LocalDate.now(),
                "A Virginia opossum, one of North America's only marsupials",
                null, null,null,null,null));
    }

    default MemberProfile createAThirdDefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Willy", null, "Wonka",
                null, "magic factory owner", null, "Chocolate Factory",
                "wonkaw@objectcomputing.com", "willy-wonka-employee", LocalDate.now(),
                "questionable employer, but gives free golden tickets",
                null, null,null,null,null));
    }

    default MemberProfile createADefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Bill PDL", null, "Johnson",
                null, "Comedic Relief PDL", memberProfile.getId(), "New York, New York",
                "billmpdl@objectcomputing.com", "mr-bill-employee-pdl",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows",
                memberProfile.getId(), null,null,null,null));
    }

    default MemberProfile createASecondDefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Sluggo PDL", null, "Simpson",
                null, "Bully Relief PDL", memberProfile.getId(), "New York, New York",
                "sluggopdl@objectcomputing.com", "sluggo-employee-pdl",
                LocalDate.now(), "is the bully in a clay figurine clown star of a parody of children's clay animation shows",
                memberProfile.getId(), null,null,null,null));
    }

    default MemberProfile createAThirdDefaultMemberProfileForPdl(MemberProfile memberProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Godzilla", null, "Godzilla",
                null, "local kaiju", memberProfile.getId(), "Tokyo, Japan",
                "godzilla@objectcomputing.com", "godzilla", LocalDate.now(),
                "is a destroyer of words",
                null, null, null,null,null));
    }

    default MemberProfile createADefaultSupervisor() {
        return getMemberProfileRepository().save(new MemberProfile("dude", null, "bro",
                null, "Supervisor Man", null, "New York, New York",
                "dubebro@objectcomputing.com", "dude-bro-supervisor",
                LocalDate.now(), "is such like a bro dude, you know?",
                null, null,null,null,null));
    }
    default MemberProfile createASupervisedAndPDLUser(MemberProfile supervisorProfile, MemberProfile pdlProfile) {
        return getMemberProfileRepository().save(new MemberProfile("Charizard", null, "Char",
                null, "Local fire hazard", pdlProfile.getId(), "New York, New York",
                "charizard@objectcomputing.com", "local-kaiju",
                LocalDate.now(), "Needs a lot of supervision due to building being ultra flammable",
                supervisorProfile.getId(), null,null,null,null));
    }
    // this user is not connected to other users in the system
    default MemberProfile createAnUnrelatedUser() {
        return getMemberProfileRepository().save(new MemberProfile("Nobody", null, " Really",
                null, "Comedic Relief", null, "New York, New York",
                "nobody@objectcomputing.com", "mr-bill-employee-unrelated",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null,null,null));
    }

    default MemberProfile createAPastTerminatedMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("past terminated", null, "user",
                null, "Bully Relief PDL", null, "New York, New York",
                "sluggopdl@objectcomputing.com", "sluggo-employee-pdl-past-terminated",
                LocalDate.now(), "is the bully in a clay figurine clown star of a parody of children's clay animation shows",
                null, LocalDate.now().minusMonths(1),null,null,null));
    }

    default MemberProfile createAFutureTerminatedMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("past terminated", null, "user",
                null, "Bully Relief PDL", null, "New York, New York",
                "sluggopdl@objectcomputing.com", "sluggo-employee-pdl-future terminated",
                LocalDate.now(), "is the bully in a clay figurine clown star of a parody of children's clay animation shows",
                null, LocalDate.now().plusDays(7),null,null,null));

    }

    default MemberProfile createADefaultMemberProfileWithBirthDay() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee-birthday", LocalDate.now(),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null, LocalDate.now(),null,null));
    }

    default MemberProfile createAPastMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee-past", LocalDate.now().minusYears(2),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null,null,null));
    }

    default MemberProfile createANewHireProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee-new", LocalDate.now().minusMonths(2),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, null,null,null,null));
    }

    default MemberProfile createATerminatedNewHireProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Bill", null, "Charles",
                null, "Comedic Relief", null, "New York, New York",
                "billm@objectcomputing.com", "mr-bill-employee-term-new", LocalDate.now().minusMonths(2),
                "is a clay figurine clown star of a parody of children's clay animation shows",
                null, LocalDate.now().minusMonths(1),null,true,null));
    }

}
