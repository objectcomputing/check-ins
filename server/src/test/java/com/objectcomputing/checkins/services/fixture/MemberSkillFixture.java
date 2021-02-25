package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;

import java.time.LocalDate;

public interface MemberSkillFixture extends RepositoryFixture{

    default MemberSkill createMemberSkill(MemberProfile memberProfile, Skill skill, String skillLevel, LocalDate lastUsedDate) {
        return getMemberSkillRepository().save(new MemberSkill(memberProfile.getId(), skill.getId(), skillLevel, lastUsedDate));
    }
}
