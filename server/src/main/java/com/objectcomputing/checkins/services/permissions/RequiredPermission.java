package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;

public @interface RequiredPermission {

    /**
     * The permission required, e.g. Can View Organization Members, Can Create/Delete Organization Members
     * @return permission
     */
    Permissions value();
}
