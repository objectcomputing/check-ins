package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;

import java.time.LocalDate;
import java.util.UUID;

public interface RolePermissionFixture extends RepositoryFixture {
    default RolePermission createDefaultCheckinRolePermission(Role roleProfile) {
        return createDefaultRolePermission(RolePermissionType.CREATECHECKIN, roleProfile);
    }

    default RolePermission createDefaultRolePermission(RolePermissionType type, Role roleProfile) {
        return getRolePermissionRepository().save(new RolePermission(type, roleProfile.getId()));
    }

    default RolePermission findRolePermission(RolePermission permission) {
        return findRolePermissionById(permission.getId());
    }

    default RolePermission findRolePermissionById(UUID uuid) {
        return getRolePermissionRepository().findById(uuid).orElse(null);
    }

}
