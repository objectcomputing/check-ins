package com.objectcomputing.checkins.services.permissions;

;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.member_roles.MemberRole;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleRepository;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionRepository;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotBlank;
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
