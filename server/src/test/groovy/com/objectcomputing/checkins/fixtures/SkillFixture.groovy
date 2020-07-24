package com.objectcomputing.checkins.fixtures


import com.objectcomputing.checkins.services.skills.Skill
import com.objectcomputing.checkins.services.skills.SkillRepository

trait SkillFixture  {

    abstract SkillRepository getSkillRepository()

    Skill saveSkill () {
        Skill skill = new Skill("testSkill", true)
        skillRepository.save(skill)

        skill
    }
}
