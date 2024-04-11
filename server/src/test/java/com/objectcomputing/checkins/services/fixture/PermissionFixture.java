package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;
;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionFixture extends RepositoryFixture, RolePermissionFixture {

    // Add MEMBER Permissions here
    List<Permission> memberPermissions = List.of(
        Permission.CAN_VIEW_FEEDBACK_REQUEST,
        Permission.CAN_CREATE_FEEDBACK_REQUEST,
        Permission.CAN_DELETE_FEEDBACK_REQUEST,
        Permission.CAN_VIEW_FEEDBACK_ANSWER,
        Permission.CAN_VIEW_PERMISSIONS,
        Permission.CAN_VIEW_CHECKINS,
        Permission.CAN_CREATE_CHECKINS,
        Permission.CAN_UPDATE_CHECKINS
    );

    // Add PDL Permissions here
    List<Permission> pdlPermissions = List.of(
        Permission.CAN_VIEW_FEEDBACK_REQUEST,
        Permission.CAN_CREATE_FEEDBACK_REQUEST,
        Permission.CAN_DELETE_FEEDBACK_REQUEST,
        Permission.CAN_VIEW_FEEDBACK_ANSWER,
        Permission.CAN_VIEW_PERMISSIONS,
        Permission.CAN_VIEW_CHECKINS,
        Permission.CAN_CREATE_CHECKINS,
        Permission.CAN_UPDATE_CHECKINS
    );

    // Add ADMIN Permissions here
    List<Permission> adminPermissions = List.of(
        Permission.CAN_VIEW_FEEDBACK_REQUEST,
        Permission.CAN_CREATE_FEEDBACK_REQUEST,
        Permission.CAN_DELETE_FEEDBACK_REQUEST,
        Permission.CAN_VIEW_FEEDBACK_ANSWER,
        Permission.CAN_DELETE_ORGANIZATION_MEMBERS,
        Permission.CAN_CREATE_ORGANIZATION_MEMBERS,
        Permission.CAN_VIEW_ROLE_PERMISSIONS,
        Permission.CAN_VIEW_PERMISSIONS,
        Permission.CAN_VIEW_SKILLS_REPORT,
        Permission.CAN_VIEW_RETENTION_REPORT,
        Permission.CAN_VIEW_ANNIVERSARY_REPORT,
        Permission.CAN_VIEW_BIRTHDAY_REPORT,
        Permission.CAN_VIEW_PROFILE_REPORT,
        Permission.CAN_CREATE_CHECKINS,
        Permission.CAN_VIEW_CHECKINS,
        Permission.CAN_UPDATE_CHECKINS,
        Permission.CAN_ASSIGN_ROLE_PERMISSIONS,
        Permission.CAN_VIEW_SKILL_CATEGORIES,
        Permission.CAN_EDIT_SKILL_CATEGORIES
    );



    default void setPermissionsForAdmin(UUID roleID) {
        adminPermissions.forEach(permission -> setRolePermission(roleID, permission));

    }

    default void setPermissionsForPdl(UUID roleID) {
        pdlPermissions.forEach(permission -> setRolePermission(roleID, permission));
    }

    default void setPermissionsForMember(UUID roleID) {
        memberPermissions.forEach(permission -> setRolePermission(roleID, permission));

    }
}
