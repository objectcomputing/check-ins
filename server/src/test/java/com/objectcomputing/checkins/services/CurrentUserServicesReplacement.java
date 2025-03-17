package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;

import java.util.List;

import jakarta.inject.Inject;
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

    @Inject
    RoleRepository roleRepository;

    @Inject
    RolePermissionRepository rolePermissionRepository;

    @Override
    public MemberProfile findOrSaveUser(String firstName,
                                        String lastName,
                                        String workEmail) {
        return null;
    }

    @Override
    public boolean hasRole(RoleType role) {
        if (currentUser != null) {
            for(Role has : roleRepository.findUserRoles(currentUser.getId())) {
                if (has.getRole().equals(role.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (currentUser != null) {
            for(Role role : roleRepository.findUserRoles(currentUser.getId())) {
                for(RolePermission perm : rolePermissionRepository.findByRole(role.getRole())) {
                    if (perm.getPermission() == permission) {
                        return true;
                    }
                }
            }
        }
        return false;
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
