package com.objectcomputing.checkins.services.permissions;

import io.micronaut.core.annotation.Introspected;

@Introspected
public enum Permission {
  CAN_VIEW_FEEDBACK_REQUEST("View feedback requests", "Feedback"),
  CAN_CREATE_FEEDBACK_REQUEST("Create feedback requests", "Feedback"),
  CAN_DELETE_FEEDBACK_REQUEST("Delete feedback requests", "Feedback"),
  CAN_VIEW_FEEDBACK_ANSWER("View feedback answers", "Feedback"),
  CAN_DELETE_ORGANIZATION_MEMBERS("Delete organization members", "User Management"),
  CAN_CREATE_ORGANIZATION_MEMBERS("Create organization members", "User Management"),
  CAN_VIEW_ROLE_PERMISSIONS("View role permissions", "Security"),
  CAN_ASSIGN_ROLE_PERMISSIONS("Assign role permissions", "Security"),
  CAN_VIEW_PERMISSIONS("View all permissions", "Security"),
  CAN_VIEW_SKILLS_REPORT("View skills report", "Reporting"),
  CAN_VIEW_RETENTION_REPORT("View retention report", "Reporting"),
  CAN_VIEW_ANNIVERSARY_REPORT("View anniversary report", "Reporting"),
  CAN_VIEW_BIRTHDAY_REPORT("View birthday report", "Reporting"),
  CAN_VIEW_PROFILE_REPORT("View profile report", "Reporting"),
  CAN_CREATE_CHECKINS("Create check-ins", "Check-ins"),
  CAN_VIEW_CHECKINS("View check-ins", "Check-ins"),
  CAN_UPDATE_CHECKINS("Update check-ins", "Check-ins"),
  CAN_VIEW_SKILL_CATEGORIES("View skill categories", "Skill Categories"),

  CAN_EDIT_SKILL_CATEGORIES("Edit skill categories", "Skill Categories");

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
}

