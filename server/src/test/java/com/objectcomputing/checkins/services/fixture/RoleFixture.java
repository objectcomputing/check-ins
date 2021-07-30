package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;

import java.util.UUID;

public interface RoleFixture extends RepositoryFixture {
    default Role createDefaultAdminRole(MemberProfile memberProfile) {
        return createDefaultRole(RoleType.ADMIN, memberProfile);
    }

    default Role createDefaultRole(RoleType type, MemberProfile memberProfile) {
        return getRoleRepository().save(new Role(type, "role description", memberProfile.getId()));
    }

    default Role findRole(Role role) {
        return findRoleById(role.getId());
    }

    default Role findRoleById(UUID uuid) {
        return getRoleRepository().findById(uuid).orElse(null);
    }

}
