package com.objectcomputing.checkins.services.role.member;

import com.objectcomputing.checkins.services.role.member.RoleMember;

import java.util.Set;
import java.util.UUID;

public interface RoleMemberServices {

    RoleMember save(RoleMember roleMember);

    RoleMember read(UUID id);

    RoleMember update(RoleMember roleMember);

    void delete(UUID id);

    Set<RoleMember> findByFields(UUID roleid, UUID memberid);
}
