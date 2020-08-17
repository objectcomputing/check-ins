package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;

import java.util.UUID;

public interface RoleFixture extends RepositoryFixture {
    default Role createDefaultRoleRepository(MemberProfile memberProfile) {
        return createDefaultRoleRepository(RoleType.ADMIN, memberProfile);
    }

    default Role createDefaultRoleRepository(RoleType type, MemberProfile memberProfile) {
        return getRoleRepository().save(new Role(type, memberProfile.getUuid()));
    }

    default Role findRole(Role role) {
        return findRoleById(role.getId());
    }

    default Role findRoleById(UUID uuid) {
        return getRoleRepository().findById(uuid).orElse(null);
    }

}
