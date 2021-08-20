package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class RoleServicesImpl implements RoleServices {

    private final RoleRepository roleRepo;

    public RoleServicesImpl(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Role save(@NotNull Role role) {

        final RoleType roleType = role.getRole();

        if (roleType == null) {
            throw new BadArgException(String.format("Invalid role %s", role));
        } else if (role.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for role", role.getId()));
        }

        return roleRepo.save(role);
    }

    public Role read(@NotNull UUID id) {
        return roleRepo.findById(id).orElse(null);
    }

    public Role update(@NotNull Role role) {
        final UUID id = role.getId();
        final RoleType roleType = role.getRole();

        if (roleType == null) {
            throw new BadArgException(String.format("Invalid role %s", role));
        } else if (id == null || roleRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate role to update with id %s", id));
        }

        return roleRepo.update(role);
    }


    public Set<Role> findUserRoles(@NotNull UUID memberId) {
        return roleRepo.findUserRoles(memberId);
    }

    public void delete(@NotNull UUID id) {
        roleRepo.deleteById(id);
    }

    public Optional<Role> findByRole(RoleType roleType) {
        return roleRepo.findByRole(roleType);
    }
}
