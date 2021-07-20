package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;

import java.util.UUID;

public interface RolePermissionFixture extends RepositoryFixture {
    default RolePermission createDefaultAdminRole(MemberProfile memberProfile) {
        return createDefaultRolePermission(RolePermissionType.ADMIN, memberProfile);
    }

    default RolePermission createDefaultRolePermission(RolePermissionType type, MemberProfile memberProfile) {
        return getRolePermissionRepository().save(new RolePermission(type, memberProfile.getId()));
    }


    default RolePermission findRole(RolePermission permission) {
        return findRoleById(permission.getId());
    }

    default RolePermission findRoleById(UUID uuid) {
        return getRolePermissionRepository().findById(uuid).orElse(null);
    }

}
