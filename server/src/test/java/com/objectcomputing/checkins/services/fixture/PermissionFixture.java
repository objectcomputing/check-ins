package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.security.permissions.Permissions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionFixture extends RepositoryFixture, RolePermissionFixture {

    // Add MEMBER Permissions here
    List<Permissions> memberPermissions = List.of(
            // Check-ins
            Permissions.CAN_CREATE_CHECKIN,
            Permissions.CAN_VIEW_CHECKIN,

            // Employee Hours
            Permissions.CAN_UPLOAD_HOURS,
            Permissions.CAN_VIEW_HOURS,

            // Feedback
            Permissions.CAN_CREATE_FEEDBACK_ANSWER,
            Permissions.CAN_CREATE_FEEDBACK_REQUEST,
            Permissions.CAN_DELETE_FEEDBACK_REQUEST,
            Permissions.CAN_VIEW_FEEDBACK_ANSWER,
            Permissions.CAN_VIEW_FEEDBACK_REQUEST,
            Permissions.CAN_VIEW_FEEDBACK_TEMPLATE,

            // Files
            Permissions.CAN_DOWNLOAD_FILES,
            Permissions.CAN_VIEW_FILES,

            // GitHub
            Permissions.CAN_CREATE_GITHUB_ISSUE,

            // Guilds
            Permissions.CAN_VIEW_GUILD,

            // Members
            Permissions.CAN_CREATE_MEMBER_SKILL,
            Permissions.CAN_VIEW_ANNIVERSARY,
            Permissions.CAN_VIEW_CURRENT_USER,
            Permissions.CAN_VIEW_MEMBER,
            Permissions.CAN_VIEW_MEMBER_BDAY,
            Permissions.CAN_VIEW_MEMBER_SKILL,

            // Opportunities
            Permissions.CAN_VIEW_OPPORTUNITIES,

            // Permissions
            Permissions.CAN_VIEW_PERMISSIONS,

            // Questions
            Permissions.CAN_CREATE_QUESTION,
            Permissions.CAN_VIEW_QUESTION,
            Permissions.CAN_VIEW_QUESTION_CATEGORY,

            // Request Notifications
            Permissions.CAN_VIEW_REQUESTS,

            // Settings
            Permissions.CAN_VIEW_SETTING,

            // Skills
            Permissions.CAN_CREATE_SKILL,
            Permissions.CAN_VIEW_SKILL,

            // Surveys
            Permissions.CAN_VIEW_SURVEY,

            // Tags
            Permissions.CAN_CREATE_TAG,
            Permissions.CAN_DELETE_TAG,
            Permissions.CAN_VIEW_TAG,

            // Teams
            Permissions.CAN_VIEW_TEAM
    );

    // Add PDL Permissions here
    List<Permissions> pdlPermissions = List.of(
            // Check-ins
            Permissions.CAN_CREATE_CHECKIN,
            Permissions.CAN_DELETE_CHECKIN,
            Permissions.CAN_VIEW_CHECKIN,

            // Check-in Documents and Private Notes
            Permissions.CAN_CREATE_SENSITIVE_DATA,
            Permissions.CAN_DELETE_SENSITIVE_DATA,
            Permissions.CAN_VIEW_SENSITIVE_DATA,

            // Employee Hours
            Permissions.CAN_UPLOAD_HOURS,
            Permissions.CAN_VIEW_HOURS,

            // Feedback
            Permissions.CAN_CREATE_FEEDBACK_ANSWER,
            Permissions.CAN_CREATE_FEEDBACK_REQUEST,
            Permissions.CAN_CREATE_FEEDBACK_TEMPLATE,
            Permissions.CAN_DELETE_FEEDBACK_REQUEST,
            Permissions.CAN_DELETE_FEEDBACK_TEMPLATE,
            Permissions.CAN_VIEW_FEEDBACK_ANSWER,
            Permissions.CAN_VIEW_FEEDBACK_REQUEST,
            Permissions.CAN_VIEW_FEEDBACK_TEMPLATE,

            // Files
            Permissions.CAN_DELETE_FILES,
            Permissions.CAN_DOWNLOAD_FILES,
            Permissions.CAN_UPLOAD_FILES,
            Permissions.CAN_VIEW_FILES,

            // GitHub
            Permissions.CAN_CREATE_GITHUB_ISSUE,

            // Guild
            Permissions.CAN_VIEW_GUILD,

            // Members
            Permissions.CAN_CREATE_MEMBER_SKILL,
            Permissions.CAN_CREATE_RETENTION_REPORT,
            Permissions.CAN_VIEW_ANNIVERSARY,
            Permissions.CAN_VIEW_CURRENT_USER,
            Permissions.CAN_VIEW_MEMBER,
            Permissions.CAN_VIEW_MEMBER_BDAY,
            Permissions.CAN_VIEW_MEMBER_SKILL,

            // Notes
            Permissions.CAN_CREATE_PRIVATE_NOTE,
            Permissions.CAN_VIEW_PRIVATE_NOTE,

            // Opportunities
            Permissions.CAN_CREATE_OPPORTUNITIES,
            Permissions.CAN_DELETE_OPPORTUNITIES,
            Permissions.CAN_VIEW_OPPORTUNITIES,

            // Permissions
            Permissions.CAN_VIEW_PERMISSIONS,

            // Questions
            Permissions.CAN_CREATE_QUESTION,
            Permissions.CAN_CREATE_QUESTION_CATEGORY,
            Permissions.CAN_DELETE_QUESTION_CATEGORY,
            Permissions.CAN_VIEW_QUESTION,
            Permissions.CAN_VIEW_QUESTION_CATEGORY,

            // Request Notifications
            Permissions.CAN_VIEW_REQUESTS,

            // Settings
            Permissions.CAN_CREATE_SETTING,
            Permissions.CAN_DELETE_SETTING,
            Permissions.CAN_VIEW_SETTING,

            // Skills
            Permissions.CAN_CREATE_SKILL,
            Permissions.CAN_DELETE_SKILL,
            Permissions.CAN_VIEW_SKILL,

            // Surveys
            Permissions.CAN_CREATE_SURVEY,
            Permissions.CAN_DELETE_SURVEY,
            Permissions.CAN_VIEW_SURVEY,

            // Tags
            Permissions.CAN_CREATE_TAG,
            Permissions.CAN_DELETE_TAG,
            Permissions.CAN_VIEW_TAG,

            // Teams
            Permissions.CAN_CREATE_TEAM,
            Permissions.CAN_DELETE_TEAM,
            Permissions.CAN_VIEW_TEAM
    );

    // Add ADMIN Permissions here
    List<Permissions> adminPermissions = List.of(
            // Check-ins
            Permissions.CAN_CREATE_CHECKIN,
            Permissions.CAN_DELETE_CHECKIN,
            Permissions.CAN_VIEW_CHECKIN,

            // Check-in Documents and Private Notes
            Permissions.CAN_CREATE_SENSITIVE_DATA,
            Permissions.CAN_DELETE_SENSITIVE_DATA,
            Permissions.CAN_VIEW_SENSITIVE_DATA,

            // Demographics
            Permissions.CAN_CREATE_DEMOGRAPHIC,
            Permissions.CAN_DELETE_DEMOGRAPHIC,
            Permissions.CAN_VIEW_DEMOGRAPHIC,

            // Employee Hours
            Permissions.CAN_UPLOAD_HOURS,
            Permissions.CAN_VIEW_HOURS,

            // Feedback
            Permissions.CAN_CREATE_FEEDBACK_ANSWER,
            Permissions.CAN_CREATE_FEEDBACK_REQUEST,
            Permissions.CAN_CREATE_FEEDBACK_TEMPLATE,
            Permissions.CAN_DELETE_FEEDBACK_REQUEST,
            Permissions.CAN_DELETE_FEEDBACK_TEMPLATE,
            Permissions.CAN_VIEW_FEEDBACK_ANSWER,
            Permissions.CAN_VIEW_FEEDBACK_REQUEST,
            Permissions.CAN_VIEW_FEEDBACK_SUGGESTION,
            Permissions.CAN_VIEW_FEEDBACK_TEMPLATE,

            // Files
            Permissions.CAN_DELETE_FILES,
            Permissions.CAN_DOWNLOAD_FILES,
            Permissions.CAN_UPLOAD_FILES,
            Permissions.CAN_VIEW_FILES,

            // GitHub
            Permissions.CAN_CREATE_GITHUB_ISSUE,

            // Guilds
            Permissions.CAN_CREATE_GUILD,
            Permissions.CAN_DELETE_GUILD,
            Permissions.CAN_VIEW_GUILD,

            // Members
            Permissions.CAN_CREATE_MEMBER,
            Permissions.CAN_CREATE_MEMBER_SKILL,
            Permissions.CAN_CREATE_RETENTION_REPORT,
            Permissions.CAN_DELETE_MEMBER,
            Permissions.CAN_DELETE_MEMBER_SKILL,
            Permissions.CAN_VIEW_ANNIVERSARY,
            Permissions.CAN_VIEW_CURRENT_USER,
            Permissions.CAN_VIEW_MEMBER,
            Permissions.CAN_VIEW_MEMBER_BDAY,
            Permissions.CAN_VIEW_MEMBER_SKILL,

            // Notes
            Permissions.CAN_CREATE_PRIVATE_NOTE,
            Permissions.CAN_VIEW_PRIVATE_NOTE,

            // Opportunities
            Permissions.CAN_CREATE_OPPORTUNITIES,
            Permissions.CAN_DELETE_OPPORTUNITIES,
            Permissions.CAN_VIEW_OPPORTUNITIES,

            // Organization Members
            Permissions.CAN_DELETE_ORGANIZATION_MEMBERS,
            Permissions.CAN_CREATE_ORGANIZATION_MEMBERS,

            // Permissions
            Permissions.CAN_VIEW_PERMISSIONS,

            // Questions
            Permissions.CAN_CREATE_QUESTION,
            Permissions.CAN_CREATE_QUESTION_CATEGORY,
            Permissions.CAN_DELETE_QUESTION_CATEGORY,
            Permissions.CAN_VIEW_QUESTION,
            Permissions.CAN_VIEW_QUESTION_CATEGORY,

            //Request Notifications
            Permissions.CAN_VIEW_REQUESTS,

            // Roles
            Permissions.CAN_CREATE_ROLE,
            Permissions.CAN_DELETE_ROLE,
            Permissions.CAN_VIEW_ROLE,
            Permissions.CAN_VIEW_ROLE_PERMISSIONS,

            // Settings
            Permissions.CAN_CREATE_SETTING,
            Permissions.CAN_DELETE_SETTING,
            Permissions.CAN_VIEW_SETTING,

            // Skills
            Permissions.CAN_CREATE_SKILL,
            Permissions.CAN_DELETE_SKILL,
            Permissions.CAN_VIEW_SKILL,

            // Surveys
            Permissions.CAN_CREATE_SURVEY,
            Permissions.CAN_DELETE_SURVEY,
            Permissions.CAN_VIEW_SURVEY,

            // Tags
            Permissions.CAN_CREATE_TAG,
            Permissions.CAN_DELETE_TAG,
            Permissions.CAN_VIEW_TAG,

            // Teams
            Permissions.CAN_CREATE_TEAM,
            Permissions.CAN_DELETE_TEAM,
            Permissions.CAN_VIEW_TEAM
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
