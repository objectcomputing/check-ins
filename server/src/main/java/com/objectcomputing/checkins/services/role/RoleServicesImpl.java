package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleRepository;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class RoleServicesImpl implements RoleServices {

    private final RoleRepository roleRepo;
    private final MemberRoleRepository memberRoleRepo;

    public RoleServicesImpl(RoleRepository roleRepo, MemberRoleRepository memberRoleRepo) {
        this.roleRepo = roleRepo;
        this.memberRoleRepo = memberRoleRepo;
    }

    public Role save(@NotNull Role role) {

        final String roleType = role.getRole();

        if (roleType == null) {
            throw new BadArgException(String.format("Invalid role %s", role));
        } else if (role.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for role", role.getId()));
        } else if (roleRepo.findByRole(roleType).isPresent()){
            throw new BadArgException(String.format("Role with name %s already exists in database", role.getRole()));
        }

        return roleRepo.save(role);
    }

    public Role read(@NotNull UUID id) {
        return roleRepo.findById(id).orElse(null);
    }

    public Role update(@NotNull Role role) {
        final UUID id = role.getId();
        final String roleType = role.getRole();

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
        memberRoleRepo.deleteByRoleId(id);
        roleRepo.deleteById(id);
    }

    public Optional<Role> findByRole(String roleType) {
        return roleRepo.findByRole(roleType);
    }

    public List<Role> findAllRoles() {
        return (List<Role>) roleRepo.findAll();
    }
}
