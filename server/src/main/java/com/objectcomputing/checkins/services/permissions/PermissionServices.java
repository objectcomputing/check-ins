package com.objectcomputing.checkins.services.permissions;

import java.util.List;

public interface PermissionServices {

    List<Permission> findAll();

    List<Permission> listOrderByPermission();
}
