package com.objectcomputing.checkins.fixtures

import com.objectcomputing.checkins.services.checkins.CheckInRepository
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository
import com.objectcomputing.checkins.services.skills.SkillRepository

import io.micronaut.context.ApplicationContext

trait RepositoriesFixture {

    abstract ApplicationContext getApplicationContext()

    SkillRepository getSkillRepository() {
        applicationContext.getBean(SkillRepository)
    }

    MemberProfileRepository getMemberProfileRepository() {
        applicationContext.getBean(MemberProfileRepository)
    }

    CheckInRepository getCheckInRepository() {
        applicationContext.getBean(CheckInRepository)
    }

}
