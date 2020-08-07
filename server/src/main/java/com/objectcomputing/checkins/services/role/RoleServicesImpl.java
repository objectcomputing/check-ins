package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RoleServicesImpl implements RoleServices {

    @Inject
    private RoleRepository roleRepo;
    @Inject
    private MemberProfileRepository memberRepo;

    public Role save(Role role) {
        Role roleRet = null;
        if (role != null) {
            final UUID memberId = role.getMemberid();
            final RoleType roleType = role.getRole();
            if (roleType == null || memberId == null) {
                throw new RoleBadArgException(String.format("Invalid role %s", role));
            } else if (role.getId() != null) {
                throw new RoleBadArgException(String.format("Found unexpected id %s for role", role.getId()));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new RoleBadArgException(String.format("Member %s doesn't exist", memberId));
            } else if (roleRepo.findByRoleAndMemberid(roleType, role.getMemberid()).isPresent()) {
                throw new RoleBadArgException(String.format("Member %s already has role %s", memberId, roleType));
            }

            roleRet = roleRepo.save(role);
        }
        return roleRet;
    }

    public Role read(@NotNull UUID id) {
        return roleRepo.findById(id).orElse(null);
    }

    public Role update(Role role) {
        Role roleRet = null;
        if (role != null) {
            final UUID id = role.getId();
            final UUID memberId = role.getMemberid();
            final RoleType roleType = role.getRole();
            if (roleType == null || memberId == null) {
                throw new RoleBadArgException(String.format("Invalid role %s", role));
            } else if (id == null || !roleRepo.findById(id).isPresent()) {
                throw new RoleBadArgException(String.format("Unable to locate role to update with id %s", id));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new RoleBadArgException(String.format("Member %s doesn't exist", memberId));
            }

            roleRet = roleRepo.update(role);
        }
        return roleRet;
    }

    public Set<Role> findByFields(RoleType role, UUID memberid) {
        Set<Role> roles = new HashSet<>();
        roleRepo.findAll().forEach(roles::add);

        if (role != null) {
            roles.retainAll(roleRepo.findByRole(role));
        }
        if (memberid != null) {
            roles.retainAll(roleRepo.findByMemberid(memberid));
        }

        return roles;
    }
}
