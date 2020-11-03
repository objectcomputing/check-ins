package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.skills.Skill;

public interface MemberSkillFixture extends RepositoryFixture{

    default MemberSkill createMemberSkill(MemberProfileEntity memberProfileEntity, Skill skill) {
        return getMemberSkillRepository().save(new MemberSkill(memberProfileEntity.getId(),skill.getId()));
    }
}
