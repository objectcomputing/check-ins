package com.objectcomputing.checkins.services.role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleServices {
    RoleResponseDTO read(UUID id);

    RoleResponseDTO save(RoleCreateDTO g);

    RoleResponseDTO update(RoleUpdateDTO g);

    Set<RoleResponseDTO> findByFields(RoleType role, UUID memberId);

    boolean delete(UUID id);



    Set<RoleResponseDTO> findByRole(RoleType role);

    Set<RoleResponseDTO> findByMemberid(UUID uuid);

    List<RoleResponseDTO> findByRoleAndMemberid(RoleType role, UUID memberId);

    void deleteByRoleAndMemberid(RoleType role, UUID memberId);

}
