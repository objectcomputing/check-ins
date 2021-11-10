package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<RolePermissionResponseDTO> findAll() {
        List<RolePermissionResponseDTO> roleInfo = new ArrayList<>();
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

            RolePermissionResponseDTO rolePermissionResponseDTO = new RolePermissionResponseDTO();
            rolePermissionResponseDTO.setRoleId(role.getId());
            rolePermissionResponseDTO.setRole(role.getRole());
            rolePermissionResponseDTO.setDescription(role.getDescription());
            rolePermissionResponseDTO.setPermissions(permissionsAssociatedWithRole);
            roleInfo.add(rolePermissionResponseDTO);
        }

        return roleInfo;
    }
}
