package com.objectcomputing.checkins.services.permissions;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Singleton
public class PermissionServicesImpl implements PermissionServices {

    private final PermissionRepository permissionRepository;

    public PermissionServicesImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permission> findUserPermissions(@NotBlank UUID id){
        return permissionRepository.findUserPermissions(id);
    }

    public List<Permission> findAll(){
        return permissionRepository.findAll();
    }

    public List<Permission> listOrderByPermission(){ return permissionRepository.listOrderByPermission(); }
}
