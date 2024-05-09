package com.objectcomputing.checkins.services.permissions;


public @interface RequiredPermission {

    /**
     * The permission required, e.g. Can View Organization Members, Can Create/Delete Organization Members
     * @return permission
     */
    Permission value();
}
