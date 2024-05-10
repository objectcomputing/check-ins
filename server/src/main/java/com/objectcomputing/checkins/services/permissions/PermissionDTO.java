package com.objectcomputing.checkins.services.permissions;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class PermissionDTO {

  private String permission;
  private String description;
  private String category;

  public PermissionDTO(Permission permission) {
    this.permission = permission.name();
    this.description = permission.getDescription();
    this.category = permission.getCategory();
  }

}

