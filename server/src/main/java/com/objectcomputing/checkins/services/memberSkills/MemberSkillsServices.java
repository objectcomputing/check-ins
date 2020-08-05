package com.objectcomputing.checkins.services.memberSkills;

import java.util.List;
import java.util.UUID;

public interface MemberSkillsServices {
    MemberSkill read(UUID uuid);

    MemberSkill save(MemberSkill m);

    MemberSkill update(MemberSkill m);

    List<MemberSkill> findByFields(UUID memberid, UUID skillid);
}
