package com.objectcomputing.checkins.security.permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.security.authentication.UserDetails;

import java.util.Collection;
import java.util.List;

public class ExtendedUserDetails extends UserDetails {

    private List<Permission> permissions;

    public ExtendedUserDetails(String username, Collection<String> roles) {
        super(username, roles);
    }

    public ExtendedUserDetails(String username, Collection<String> roles, List<Permission> permissions) {
        super(username, roles);
        this.permissions = permissions;
    }


    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
