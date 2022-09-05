package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleRepository;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

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

        validate(roleType != null).orElseThrow(() -> {
            throw new BadArgException("Invalid role %s", role);
        });
        validate(role.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Found unexpected id %s for role", role.getId());
        });
        validate(roleRepo.findByRole(roleType).isEmpty()).orElseThrow(() -> {
            throw new BadArgException("Role with name %s already exists in database", role.getRole());
        });

        return roleRepo.save(role);
    }

    public Role read(@NotNull UUID id) {
        return roleRepo.findById(id).orElse(null);
    }

    public Role update(@NotNull Role role) {
        final UUID id = role.getId();
        final String roleType = role.getRole();

        validate(roleType != null).orElseThrow(() -> {
            throw new BadArgException("Invalid role %s", role);
        });
        validate(id != null && roleRepo.findById(id).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Unable to locate role to update with id %s", id);
        });

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
