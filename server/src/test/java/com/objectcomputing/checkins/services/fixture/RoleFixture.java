package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;

import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.member_roles.MemberRole;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleId;

import java.util.UUID;

public interface RoleFixture extends RepositoryFixture {
    default Role createRole(Role role) {
        return getRoleRepository().save(new Role(role.getRole(), role.getDescription()));
    }

    default Role createAndAssignAdminRole(MemberProfile memberProfile) {
        return createAndAssignRole(RoleType.ADMIN, memberProfile);
    }

    // TODO phase out RoleType
    default Role createAndAssignRole(RoleType type, MemberProfile memberProfile) {
        Role role = getRoleRepository().save(new Role(type.name(), "role description"));
        getMemberRoleRepository()
                .saveByIds(memberProfile.getId(), role.getId());
        return role;
    }

    default Role createAndAssignRole(String roleName, MemberProfile memberProfile) {
        Role role = getRoleRepository().save(new Role(roleName, "role description"));
        getMemberRoleRepository()
                .save(new MemberRole(memberProfile.getId(), role.getId()));

        return role;
    }

    default Role findRole(Role role) {
        return findRoleById(role.getId());
    }

    default Role findRoleById(UUID uuid) {
        return getRoleRepository().findById(uuid).orElse(null);
    }

}
