package com.objectcomputing.checkins.services.permissions;

import java.util.List;
import java.util.UUID;

public interface PermissionServices {
    List<Permission> findUserPermissions(UUID id);

    List<Permission> findCurrentUserPermissions(UUID id);

    List<Permission> findAll();

    List<Permission> listOrderByPermission();
}
