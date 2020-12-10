package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.services.skills.Skill;

import java.util.Set;
import java.util.UUID;

public interface MemberSkillServices {

    MemberSkill read(UUID uuid);

    MemberSkill save(MemberSkill m);

    MemberSkill update(MemberSkill memberSkill);

    Set<MemberSkill> findByFields(UUID memberid, UUID skillid);

    void delete(UUID id);
}
