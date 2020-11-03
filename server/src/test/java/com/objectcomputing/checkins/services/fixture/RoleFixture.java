package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;

import java.util.UUID;

public interface RoleFixture extends RepositoryFixture {
    default Role createDefaultRole(MemberProfileEntity memberProfileEntity) {
        return createDefaultRole(RoleType.ADMIN, memberProfileEntity);
    }

    default Role createDefaultRole(RoleType type, MemberProfileEntity memberProfileEntity) {
        return getRoleRepository().save(new Role(type, memberProfileEntity.getId()));
    }

    default Role findRole(Role role) {
        return findRoleById(role.getId());
    }

    default Role findRoleById(UUID uuid) {
        return getRoleRepository().findById(uuid).orElse(null);
    }

}
