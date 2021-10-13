package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.Permissions;

import java.util.List;
import java.util.UUID;

public interface PermissionFixture extends RepositoryFixture, RolePermissionFixture {

    // Add MEMBER Permissions here
    List<Permissions> memberPermissions = List.of(
            Permissions.CAN_VIEW_FEEDBACK,
            Permissions.CAN_CREATE_FEEDBACK,
            Permissions.CAN_DELETE_FEEDBACK
    );

    // Add PDL Permissions here
    List<Permissions> pdlPermissions = List.of(
            Permissions.CAN_VIEW_FEEDBACK,
            Permissions.CAN_CREATE_FEEDBACK,
            Permissions.CAN_DELETE_FEEDBACK
    );

    // Add ADMIN Permissions here
    List<Permissions> adminPermissions = List.of(
            Permissions.CAN_VIEW_FEEDBACK,
            Permissions.CAN_CREATE_FEEDBACK,
            Permissions.CAN_DELETE_FEEDBACK,
            Permissions.CAN_DELETE_ORGANIZATION_MEMBERS,
            Permissions.CAN_CREATE_ORGANIZATION_MEMBERS
    );

    default Permission createADefaultPermission() {
        return getPermissionRepository().save(new Permission(null,"A sample permission", "sample description"));
    }

    default Permission createADifferentPermission() {
        return getPermissionRepository().save(new Permission(null,"Other sample permission", "Other sample description"));
    }

    default Permission createACustomPermission(String name) {
        return getPermissionRepository().save(new Permission(null, name, null));
    }

    default void saveAllPermissions() {
        for(Permissions permissions : Permissions.values()) {
            getPermissionRepository().save(new Permission(null, permissions.name(), null));
        }
    }

    default void setPermissionsForAdmin(UUID roleID) {
        List<Permission> permissions = getPermissionRepository().findAll();
        for(Permissions adminPermission : adminPermissions) {
            setRolePermission(roleID, permissions.stream().filter(s -> s.getPermission().equals(adminPermission.name())).findFirst().get().getId());
        }
    }

    default void setPermissionsForPdl(UUID roleID) {
        List<Permission> permissions = getPermissionRepository().findAll();
        for(Permissions pdlPermission : pdlPermissions) {
            setRolePermission(roleID, permissions.stream().filter(s -> s.getPermission().equals(pdlPermission.name())).findFirst().get().getId());
        }
    }

    default void setPermissionsForMember(UUID roleID) {
        List<Permission> permissions = getPermissionRepository().findAll();
        for(Permissions memberPermission : memberPermissions) {
            setRolePermission(roleID, permissions.stream().filter(s -> s.getPermission().equals(memberPermission.name())).findFirst().get().getId());
        }
    }
}
