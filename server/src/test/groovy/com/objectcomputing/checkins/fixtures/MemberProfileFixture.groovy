package com.objectcomputing.checkins.fixtures

import com.objectcomputing.checkins.services.memberprofile.MemberProfile
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository

import java.time.LocalDate

trait MemberProfileFixture {

    abstract MemberProfileRepository getMemberProfileRepository()
    private static LocalDate testDate = LocalDate.now()

    MemberProfile saveMemberProfile() {
        MemberProfile memberProfile = new MemberProfile("testName", "testRole", null, "testLocation",
        "testEmail", "testInsperityId", testDate, "testBio")
        memberProfileRepository.save(memberProfile)

        memberProfile
    }

}
