package com.objectcomputing.checkins.services.permissions;

import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.List;

@Singleton
public class PermissionServicesImpl implements PermissionServices {

  public List<Permission> findAll() {
    return Arrays.asList(Permission.values());
  }

  public List<Permission> listOrderByPermission() {
    return Arrays.stream(Permission.values()).sorted().toList();
  }
}
