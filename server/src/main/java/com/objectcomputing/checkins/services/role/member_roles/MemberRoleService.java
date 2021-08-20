package com.objectcomputing.checkins.services.role.member_roles;

import java.util.List;
import java.util.UUID;

public interface MemberRoleService {
    List<MemberRole> findAll();

    void deleteById(String memberid, String roleid);

    void removeMemberFromRoles(String memberid);

    MemberRole save(UUID memberid, UUID roleid);


}
