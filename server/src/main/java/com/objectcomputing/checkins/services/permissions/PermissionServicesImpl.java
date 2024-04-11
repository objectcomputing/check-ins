package com.objectcomputing.checkins.services.permissions;

import jakarta.inject.Singleton;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class PermissionServicesImpl implements PermissionServices {


  public List<Permission> findAll() {
    return Arrays.stream(Permission.values()).collect(Collectors.toList());
  }

  public List<Permission> listOrderByPermission() {
    return Arrays.stream(Permission.values()).sorted().collect(Collectors.toList());
  }
}
