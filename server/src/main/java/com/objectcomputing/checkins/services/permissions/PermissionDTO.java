package com.objectcomputing.checkins.services.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

@Introspected
public class PermissionDTO {

  private String permission;
  private String description;

  public PermissionDTO() {}

  public PermissionDTO(String permission, String description, String category) {
    this.permission = permission;
    this.description = description;
    this.category = category;
  }

  private String category;

  public PermissionDTO(Permission permission) {
    this.permission = permission.name();
    this.description = permission.getDescription();
    this.category = permission.getCategory();
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PermissionDTO that = (PermissionDTO) o;
    return Objects.equals(permission, that.permission) && Objects.equals(description, that.description) && Objects.equals(category, that.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(permission, description, category);
  }
}

