package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;

public interface PermissionFixture extends RepositoryFixture {
    default Permission createADefaultPermission(){
        return getPermissionRepository().save(new Permission(null,"A sample permission", "sample description"));
    }

    default Permission createADifferentPermission(){
        return getPermissionRepository().save(new Permission(null,"Other sample permission", "Other sample description"));
    }

    default Permission createACustomPermission(String name){
        return getPermissionRepository().save(new Permission(null, name, null));
    }
}
