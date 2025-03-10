package com.objectcomputing.checkins.services.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.micronaut.core.annotation.Introspected;

@Introspected
@JsonSerialize(using = PermissionSerializer.class)
public enum Permission {
  CAN_VIEW_FEEDBACK_REQUEST("View feedback requests", "Feedback"),
  CAN_CREATE_FEEDBACK_REQUEST("Create feedback requests", "Feedback"),
  CAN_DELETE_FEEDBACK_REQUEST("Delete feedback requests", "Feedback"),
  CAN_ADMINISTER_FEEDBACK_REQUEST("Administer feedback requests", "Feedback"),
  CAN_CREATE_KUDOS("Create kudos", "Feedback"),
  CAN_ADMINISTER_KUDOS("Administer kudos", "Feedback"),
  CAN_VIEW_FEEDBACK_ANSWER("View feedback answers", "Feedback"),
  CAN_ADMINISTER_FEEDBACK_ANSWER("Administer feedback answers", "Feedback"),
  CAN_ADMINISTER_FEEDBACK_TEMPLATES("Administer feedback templates", "Feedback"),
  CAN_SEND_EMAIL("Send email", "Feedback"),
  CAN_VIEW_TERMINATED_MEMBERS("Can view the profiles of terminated members", "User Management"),
  CAN_EDIT_ALL_ORGANIZATION_MEMBERS("Edit all member profiles", "User Management"),
  CAN_DELETE_ORGANIZATION_MEMBERS("Delete organization members", "User Management"),
  CAN_CREATE_ORGANIZATION_MEMBERS("Create organization members", "User Management"),
  CAN_IMPERSONATE_MEMBERS("Impersonate organization members", "Security"),
  CAN_VIEW_ROLE_PERMISSIONS("View role permissions", "Security"),
  CAN_ASSIGN_ROLE_PERMISSIONS("Assign role permissions", "Security"),
  CAN_EDIT_MEMBER_ROLES("Edit member roles", "Security"),
  CAN_VIEW_PERMISSIONS("View all permissions", "Security"),
  CAN_VIEW_SKILLS_REPORT("View skills report", "Reporting"),
  CAN_VIEW_RETENTION_REPORT("View retention report", "Reporting"),
  CAN_VIEW_ANNIVERSARY_REPORT("View anniversary report", "Reporting"),
  CAN_VIEW_BIRTHDAY_REPORT("View birthday report", "Reporting"),
  CAN_VIEW_PROFILE_REPORT("View profile report", "Reporting"),
  CAN_VIEW_CHECKINS_REPORT("View checkins report", "Reporting"),
  CAN_CREATE_MERIT_REPORT("Create Merit Reports", "Reporting"),
  CAN_UPLOAD_HOURS("Upload Hours", "Reporting"),
  CAN_VIEW_ALL_UPLOADED_HOURS("View all uploaded hours", "Reporting"),
  CAN_CREATE_CHECKINS("Create check-ins", "Check-ins"),
  CAN_VIEW_CHECKINS("View check-ins", "Check-ins"),
  CAN_UPDATE_CHECKINS("Update check-ins", "Check-ins"),
  CAN_VIEW_SKILL_CATEGORIES("View skill categories", "Skill Categories"),
  CAN_VIEW_PRIVATE_NOTE("View check-ins private notes", "Check-ins"),
  CAN_UPDATE_PRIVATE_NOTE("Update check-ins private notes", "Check-ins"),
  CAN_CREATE_PRIVATE_NOTE("Create check-ins private notes", "Check-ins"),
  CAN_VIEW_CHECKIN_DOCUMENT("View check-ins document", "Check-ins"),
  CAN_ADMINISTER_CHECKIN_DOCUMENTS("Administer check-ins documents", "Check-ins"),
  CAN_UPDATE_CHECKIN_DOCUMENT("Update check-ins document", "Check-ins"),
  CAN_CREATE_CHECKIN_DOCUMENT("Create check-ins document", "Check-ins"),
  CAN_DELETE_CHECKIN_DOCUMENT("Delete check-ins document", "Check-ins"),
  CAN_VIEW_ALL_CHECKINS("View all check-ins", "Check-ins"),
  CAN_UPDATE_ALL_CHECKINS("Update all check-ins, including completed check-ins", "Check-ins"),
  CAN_EDIT_SKILL_CATEGORIES("Edit skill categories", "Skill Categories"),
  CAN_EDIT_SKILLS("Edit skills", "Skills"),
  CAN_CREATE_REVIEW_ASSIGNMENTS("Create review assignments", "Reviews"),
  CAN_VIEW_REVIEW_ASSIGNMENTS("View review assignments", "Reviews"),
  CAN_UPDATE_REVIEW_ASSIGNMENTS("Update review assignments", "Reviews"),
  CAN_DELETE_REVIEW_ASSIGNMENTS("Delete review assignments", "Reviews"),
  CAN_VIEW_REVIEW_PERIOD("View review periods", "Review Periods"),
  CAN_CREATE_REVIEW_PERIOD("Create review periods", "Review Periods"),
  CAN_UPDATE_REVIEW_PERIOD("Update review periods", "Review Periods"),
  CAN_LAUNCH_REVIEW_PERIOD("Launch review periods", "Review Periods"),
  CAN_CLOSE_REVIEW_PERIOD("Close review periods", "Review Periods"),
  CAN_DELETE_REVIEW_PERIOD("Delete review periods", "Review Periods"),
  CAN_ADMINISTER_SETTINGS("Add or edit settings", "Settings"),
  CAN_VIEW_SETTINGS("View settings", "Settings"),
  CAN_VIEW_ALL_PULSE_RESPONSES("View pulse responses", "Reporting"),
  CAN_MANAGE_CERTIFICATIONS("Manage certifications", "Certifications"),
  CAN_MANAGE_EARNED_CERTIFICATIONS("Manage earned certifications", "Certifications"),
  CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS("Administer volunteering organizations", "Volunteering"),
  CAN_ADMINISTER_VOLUNTEERING_RELATIONSHIPS("Administer volunteering relationships", "Volunteering"),
  CAN_ADMINISTER_VOLUNTEERING_EVENTS("Administer volunteering events", "Volunteering"),
  CAN_ADMINISTER_GUILDS("Administer guilds", "Guilds"),
  CAN_ADMINISTER_TEAMS("Administer teams", "Teams"),
  CAN_ADMINISTER_DOCUMENTATION("Administer documentation and role documentation", "Documentation");

  private final String description;
  private final String category;

  Permission(String description, String category) {
    this.description = description;
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public String getCategory() {
    return category;
  }

  // Use the fromName method as @JsonCreator
  @JsonCreator
  public static Permission fromName(String name) {
    for (Permission permission : values()) {
      if (permission.name().equalsIgnoreCase(name)) {
        return permission;
      }
    }
    throw new UnsupportedOperationException(String.format("Unknown permission: '%s'", name));
  }
}
