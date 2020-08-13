package com.objectcomputing.checkins.services.memberSkill;

import java.util.Set;
import java.util.UUID;

public interface MemberSkillServices {

    MemberSkill read(UUID uuid);

//    Set<MemberSkill> readAll();

    MemberSkill save(MemberSkill m);

    MemberSkill update(MemberSkill m);

    Set<MemberSkill> findByFields(UUID memberid, UUID skillid);

    void delete(UUID id);
}
