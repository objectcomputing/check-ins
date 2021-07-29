package com.objectcomputing.checkins.services.role;

import java.util.Set;
import java.util.UUID;

public interface RoleServices {

    Role save(Role role);

    Role read(UUID id);

    Role update(Role role);

    Set<Role> findByFields(RoleType role);

    Set<RoleResponseDTO> findByMemberid(UUID uuid);

    Set<RoleResponseDTO> findByRoleAndMemberid(RoleType role, UUID memberid);

    void delete(UUID id);
}
