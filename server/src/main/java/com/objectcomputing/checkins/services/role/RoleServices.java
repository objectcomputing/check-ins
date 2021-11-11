package com.objectcomputing.checkins.services.role;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleServices {

    Role save(Role role);

    Role read(UUID id);

    Role update(Role role);

    void delete(UUID id);

    Optional<Role> findByRole(String role);

    Set<Role> findUserRoles(UUID memberId);

    List<Role> findAllRoles();
}
