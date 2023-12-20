package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;

import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class RolePermissionServicesImpl implements RolePermissionServices {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleServices roleServices;
    private final PermissionServices permissionServices;

    public RolePermissionServicesImpl(RolePermissionRepository rolePermissionRepository,
                                      RoleServices roleServices,
                                      PermissionServices permissionServices) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleServices = roleServices;
        this.permissionServices = permissionServices;
    }

    public List<RolePermissionsResponseDTO> findAll() {
        List<RolePermissionsResponseDTO> roleInfo = new ArrayList<>();
        List<RolePermission> records =  rolePermissionRepository.findAll();
        List<Role> roles = roleServices.findAllRoles();
        List<Permission> permissions = permissionServices.findAll();

        for(Role role : roles) {
            List<Permission> permissionsAssociatedWithRole = new ArrayList<>();
            for(RolePermission rolePermission : records) {
                if(role.getId().equals(rolePermission.getRoleId())) {
                    Optional<Permission> permission = permissions.stream().filter(s-> s.getId().equals(rolePermission.getPermissionId())).findFirst();
                    permission.ifPresent(permissionsAssociatedWithRole::add);
                }
            }

            RolePermissionsResponseDTO rolePermissionsResponseDTO = new RolePermissionsResponseDTO();
            rolePermissionsResponseDTO.setRoleId(role.getId());
            rolePermissionsResponseDTO.setRole(role.getRole());
            rolePermissionsResponseDTO.setDescription(role.getDescription());
            rolePermissionsResponseDTO.setPermissions(permissionsAssociatedWithRole);
            roleInfo.add(rolePermissionsResponseDTO);
        }

        return roleInfo;
    }

    @Override
    public RolePermission save(UUID roleId, UUID permissionId) {
        rolePermissionRepository.saveByIds(roleId.toString(), permissionId.toString());
        RolePermission saved = rolePermissionRepository.findByIds(roleId.toString(), permissionId.toString()).get(0);
        return saved;
    }

    @Override
    public void delete(UUID roleId, UUID permissionId) {
        rolePermissionRepository.deleteByIds(roleId.toString(), permissionId.toString());
    }
}
