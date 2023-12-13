package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.security.permissions.Permissions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionFixture extends RepositoryFixture, RolePermissionFixture {

    // Add MEMBER Permissions here
    List<Permissions> memberPermissions = List.of(
        Permissions.CAN_VIEW_FEEDBACK_REQUEST,
        Permissions.CAN_CREATE_FEEDBACK_REQUEST,
        Permissions.CAN_DELETE_FEEDBACK_REQUEST,
        Permissions.CAN_VIEW_FEEDBACK_ANSWER,
        Permissions.CAN_VIEW_PERMISSIONS,
        Permissions.CAN_VIEW_CHECKINS,
        Permissions.CAN_CREATE_CHECKINS,
        Permissions.CAN_UPDATE_CHECKINS
    );

    // Add PDL Permissions here
    List<Permissions> pdlPermissions = List.of(
        Permissions.CAN_VIEW_FEEDBACK_REQUEST,
        Permissions.CAN_CREATE_FEEDBACK_REQUEST,
        Permissions.CAN_DELETE_FEEDBACK_REQUEST,
        Permissions.CAN_VIEW_FEEDBACK_ANSWER,
        Permissions.CAN_VIEW_PERMISSIONS,
        Permissions.CAN_VIEW_CHECKINS,
        Permissions.CAN_CREATE_CHECKINS,
        Permissions.CAN_UPDATE_CHECKINS,
        Permissions.CAN_VIEW_CHECKINS_ELEVATED
    );

    // Add ADMIN Permissions here
    List<Permissions> adminPermissions = List.of(
        Permissions.CAN_VIEW_FEEDBACK_REQUEST,
        Permissions.CAN_CREATE_FEEDBACK_REQUEST,
        Permissions.CAN_DELETE_FEEDBACK_REQUEST,
        Permissions.CAN_VIEW_FEEDBACK_ANSWER,
        Permissions.CAN_DELETE_ORGANIZATION_MEMBERS,
        Permissions.CAN_CREATE_ORGANIZATION_MEMBERS,
        Permissions.CAN_VIEW_ROLE_PERMISSIONS,
        Permissions.CAN_VIEW_PERMISSIONS,
        Permissions.CAN_VIEW_SKILLS_REPORT,
        Permissions.CAN_VIEW_RETENTION_REPORT,
        Permissions.CAN_VIEW_ANNIVERSARY_REPORT,
        Permissions.CAN_VIEW_BIRTHDAY_REPORT,
        Permissions.CAN_VIEW_PROFILE_REPORT,
        Permissions.CAN_CREATE_CHECKINS,
        Permissions.CAN_VIEW_CHECKINS,
        Permissions.CAN_UPDATE_CHECKINS,
        Permissions.CAN_VIEW_CHECKINS_ELEVATED,
        Permissions.CAN_ASSIGN_ROLE_PERMISSIONS
    );

    default Permission createACustomPermission(Permissions perm) {
        return getPermissionRepository().save(new Permission(null, perm.name(), null));
    }

    default void saveAllPermissions() {
        for(Permissions permissions : Permissions.values()) {
            getPermissionRepository().save(new Permission(null, permissions.name(), null));
        }
    }

    default void setPermissionsForAdmin(UUID roleID) {
        List<Permission> permissions = getPermissionRepository().findAll();
        for(Permissions adminPermission : adminPermissions) {
            Optional<Permission> permission = permissions.stream().filter(s -> s.getPermission().equals(adminPermission.name())).findFirst();
            permission.ifPresent(value -> setRolePermission(roleID, value.getId()));
        }
    }

    default void setPermissionsForPdl(UUID roleID) {
        List<Permission> permissions = getPermissionRepository().findAll();
        for(Permissions pdlPermission : pdlPermissions) {
            Optional<Permission> permission = permissions.stream().filter(s -> s.getPermission().equals(pdlPermission.name())).findFirst();
            permission.ifPresent(value -> setRolePermission(roleID, value.getId()));
        }
    }

    default void setPermissionsForMember(UUID roleID) {
        List<Permission> permissions = getPermissionRepository().findAll();
        for(Permissions memberPermission : memberPermissions) {
            Optional<Permission> permission = permissions.stream().filter(s -> s.getPermission().equals(memberPermission.name())).findFirst();
            permission.ifPresent(value -> setRolePermission(roleID, value.getId()));
        }
    }
}
