package com.objectcomputing.checkins.services.role;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleServices {
    RoleResponseDTO read(UUID id);

    RoleResponseDTO save(RoleCreateDTO g);

    RoleResponseDTO update(RoleUpdateDTO g);

    Set<RoleResponseDTO> findByFields(RoleType role, UUID memberId);

    boolean delete(UUID id);



    List<RoleResponseDTO> findByRole(RoleType role);

    List<RoleResponseDTO> findByMemberid(UUID uuid);

    Optional<RoleResponseDTO> findByRoleAndMemberid(RoleType role, UUID memberId);

    void deleteByRoleAndMemberid(RoleType role, UUID memberId);

//    @Override
//    <S extends RoleResponseDTO> List<S> saveAll(@Valid @NotNull Iterable<S> entities);
//
//    @Override
//    <S extends RoleResponseDTO> S save(@Valid @NotNull @NonNull S entity);
}
