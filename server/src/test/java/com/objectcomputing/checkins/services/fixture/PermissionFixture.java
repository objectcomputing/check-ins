package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionFixture extends RolePermissionFixture {

    // Add MEMBER Permissions here
    List<Permission> memberPermissions = List.of(
        Permission.CAN_VIEW_FEEDBACK_REQUEST,
        Permission.CAN_CREATE_FEEDBACK_REQUEST,
        Permission.CAN_DELETE_FEEDBACK_REQUEST,
        Permission.CAN_VIEW_FEEDBACK_ANSWER,
        Permission.CAN_VIEW_PERMISSIONS,
        Permission.CAN_VIEW_CHECKINS,
        Permission.CAN_CREATE_CHECKINS,
        Permission.CAN_UPDATE_CHECKINS,
        Permission.CAN_ADMINISTER_SETTINGS,
        Permission.CAN_VIEW_SETTINGS,
        Permission.CAN_VIEW_REVIEW_PERIOD
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
        Permission.CAN_UPDATE_CHECKINS,
        Permission.CAN_VIEW_PRIVATE_NOTE,
        Permission.CAN_CREATE_PRIVATE_NOTE,
        Permission.CAN_UPDATE_PRIVATE_NOTE,
        Permission.CAN_CREATE_CHECKIN_DOCUMENT,
        Permission.CAN_VIEW_CHECKIN_DOCUMENT,
        Permission.CAN_UPDATE_CHECKIN_DOCUMENT,
        Permission.CAN_ADMINISTER_SETTINGS,
        Permission.CAN_VIEW_SETTINGS,
        Permission.CAN_VIEW_REVIEW_PERIOD
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
        Permission.CAN_EDIT_SKILL_CATEGORIES,
        Permission.CAN_VIEW_PRIVATE_NOTE,
        Permission.CAN_CREATE_PRIVATE_NOTE,
        Permission.CAN_UPDATE_PRIVATE_NOTE,
        Permission.CAN_CREATE_CHECKIN_DOCUMENT,
        Permission.CAN_VIEW_CHECKIN_DOCUMENT,
        Permission.CAN_UPDATE_CHECKIN_DOCUMENT,
        Permission.CAN_DELETE_CHECKIN_DOCUMENT,
        Permission.CAN_VIEW_ALL_CHECKINS,
        Permission.CAN_UPDATE_ALL_CHECKINS,
        Permission.CAN_CREATE_REVIEW_ASSIGNMENTS,
        Permission.CAN_VIEW_REVIEW_ASSIGNMENTS,
        Permission.CAN_UPDATE_REVIEW_ASSIGNMENTS,
        Permission.CAN_DELETE_REVIEW_ASSIGNMENTS,
        Permission.CAN_ADMINISTER_SETTINGS,
        Permission.CAN_VIEW_SETTINGS,
        Permission.CAN_VIEW_REVIEW_PERIOD,
        Permission.CAN_CREATE_REVIEW_PERIOD,
        Permission.CAN_UPDATE_REVIEW_PERIOD,
        Permission.CAN_LAUNCH_REVIEW_PERIOD,
        Permission.CAN_CLOSE_REVIEW_PERIOD,
        Permission.CAN_DELETE_REVIEW_PERIOD,
        Permission.CAN_VIEW_ALL_PULSE_RESPONSES,
        Permission.CAN_MANAGE_CERTIFICATIONS,
        Permission.CAN_MANAGE_EARNED_CERTIFICATIONS,
        Permission.CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS,
        Permission.CAN_ADMINISTER_VOLUNTEERING_RELATIONSHIPS,
        Permission.CAN_ADMINISTER_VOLUNTEERING_EVENTS,
        Permission.CAN_ADMINISTER_DOCUMENTATION
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
