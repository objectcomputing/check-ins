package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;

public interface PermissionFixture extends RepositoryFixture {
    default Permission createADefaultPermission(){
        return getPermissionRepository().save(new Permission("A sample permission"));
    }

    default Permission createADifferentPermission(){
        return getPermissionRepository().save(new Permission("Other sample permission"));
    }

    default Permission createACustomPermission(String name){
        return getPermissionRepository().save(new Permission(name));
    }
}
