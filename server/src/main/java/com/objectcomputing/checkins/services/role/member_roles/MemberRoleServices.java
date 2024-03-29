package com.objectcomputing.checkins.services.role.member_roles;


import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRoleServices {

    List<MemberRole> findAll();

    MemberRole saveByIds(UUID memberid, UUID roleid);

    void delete(@NotNull MemberRoleId id);

    void removeMemberFromRoles(UUID memberid);


    @NonNull
    Optional<MemberRole> findById(@NotNull MemberRoleId memberRoleId);

    Optional<MemberRole> findByMemberId(@NotNull UUID memberId);

    void removeAllByRoleId(UUID roleId);



}
