package com.objectcomputing.checkins.services.role.member_roles;


import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRoleServices {

    List<MemberRole> findAll();

    MemberRole saveByIds(UUID memberid, UUID roleid);

//    void deleteById(String memberid, String roleid);

    void removeMemberFromRoles(UUID memberid);


    @NonNull
    Optional<MemberRole> findById(@NotNull MemberRoleId memberRoleId);

    void removeAllByRoleId(UUID roleId);



}
