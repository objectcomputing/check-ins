package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Singleton
public class PermissionServicesImpl implements PermissionServices {

    private final PermissionRepository permissionRepository;
    private final CurrentUserServices currentUserServices;

    public PermissionServicesImpl(PermissionRepository permissionRepository, CurrentUserServices currentUserServices) {
        this.permissionRepository = permissionRepository;
        this.currentUserServices = currentUserServices;
    }

    public List<Permission> findUserPermissions(@NotBlank UUID id){
        return permissionRepository.findUserPermissions(id);
    }

    public List<Permission> findCurrentUserPermissions(@NotBlank UUID id) {
        // Only the current user can retrieve their own permissions (or admins)
        if (currentUserServices.getCurrentUser().getId().equals(id) || currentUserServices.isAdmin()) {
            return permissionRepository.findUserPermissions(id);
        } else {
            throw new PermissionException("You are not allowed to do this operation");
        }
    }

    public List<Permission> findAll(){
        return permissionRepository.findAll();
    }

    public List<Permission> listOrderByPermission(){ return permissionRepository.listOrderByPermission(); }
}
