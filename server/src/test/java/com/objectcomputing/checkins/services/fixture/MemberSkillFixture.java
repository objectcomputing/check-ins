package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;

public interface MemberSkillFixture extends RepositoryFixture{

    default MemberSkill createMemberSkill(MemberProfile memberProfile, Skill skill) {
        return getMemberSkillRepository().save(new MemberSkill(memberProfile.getUuid(),skill.getId()));
    }
}
