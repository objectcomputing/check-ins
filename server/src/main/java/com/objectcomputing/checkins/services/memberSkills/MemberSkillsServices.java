package com.objectcomputing.checkins.services.memberSkills;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MemberSkillsServices {

    MemberSkill read(UUID uuid);

    Set<MemberSkill> readAll();

    MemberSkill save(MemberSkill m);

    MemberSkill update(MemberSkill m);

    List<MemberSkill> findByFields(UUID memberid, UUID skillid);

    void delete(UUID id);
}
