package com.objectcomputing.checkins.fixtures

import com.objectcomputing.checkins.services.memberprofile.MemberProfile
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository

import java.sql.Date

trait MemberProfileFixture {

    abstract MemberProfileRepository getMemberProfileRepository()
    private static Date testDate = new Date(System.currentTimeMillis())

    MemberProfile saveMemberProfile() {
        MemberProfile memberProfile = new MemberProfile("testName", "testRole", UUID.randomUUID(), "testLocation",
        "testEmail", "testInsperityId", testDate, "testBio")
        memberProfileRepository.save(memberProfile)

        memberProfile
    }

}
