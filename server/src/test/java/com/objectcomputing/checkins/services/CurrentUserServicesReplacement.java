package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;

import java.util.List;

import jakarta.inject.Singleton;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.env.Environment;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

@Singleton
@Replaces(CurrentUserServices.class)
@Requires(property = "replace.currentuserservices", value = StringUtils.TRUE)
public class CurrentUserServicesReplacement implements CurrentUserServices {
    public MemberProfile currentUser;
    public List<RoleType> roles;
    public List<Permission> permissions;

    @Override
    public MemberProfile findOrSaveUser(String firstName,
                                        String lastName,
                                        String workEmail) {
        return null;
    }

    @Override
    public boolean hasRole(RoleType role) {
        return roles == null ? false : roles.contains(role);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return permissions == null ? false : permissions.contains(permission);
    }

    @Override
    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN);
    }

    @Override
    public MemberProfile getCurrentUser() {
        return currentUser;
    }
}
