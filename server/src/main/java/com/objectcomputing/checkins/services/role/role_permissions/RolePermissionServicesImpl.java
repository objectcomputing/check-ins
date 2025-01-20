package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionDTO;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.RoleServices;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
@CacheConfig("role-permission-cache")
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
                    Optional<Permission> permission = permissions.stream().filter(s-> s.equals(rolePermission.getPermission())).findFirst();
                    permission.ifPresent(permissionsAssociatedWithRole::add);
                }
            }

            RolePermissionsResponseDTO rolePermissionsResponseDTO = new RolePermissionsResponseDTO();
            rolePermissionsResponseDTO.setRoleId(role.getId());
            rolePermissionsResponseDTO.setRole(role.getRole());
            rolePermissionsResponseDTO.setDescription(role.getDescription());
            rolePermissionsResponseDTO.setPermissions(permissionsAssociatedWithRole.stream().map(PermissionDTO::new).toList());
            roleInfo.add(rolePermissionsResponseDTO);
        }

        return roleInfo;
    }

    @CacheInvalidate(all = true)
    @Override
    public RolePermission save(UUID roleId, Permission permissionId) {
        rolePermissionRepository.saveByIds(roleId.toString(), permissionId);
        RolePermission saved = rolePermissionRepository.findByIds(roleId.toString(), permissionId).get(0);
        return saved;
    }

    @CacheInvalidate(all = true)
    @Override
    public void delete(UUID roleId, Permission permissionId) {
        // Disallow removal of Assign Role Permissions from ADMIN
        if (permissionId == Permission.CAN_ASSIGN_ROLE_PERMISSIONS) {
          Role role = roleServices.read(roleId);
          if (role.getRole().equals(RoleType.Constants.ADMIN_ROLE)) {
              throw new BadArgException("Cannot remove Assign Role Permissions from ADMIN");
          }
        }
        rolePermissionRepository.deleteByIds(roleId.toString(), permissionId);
    }

    @Override
    public List<RolePermission> findByRoleId(UUID roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }


    @Cacheable
    @Override
    public List<RolePermission> findByRole(String role) {
        return rolePermissionRepository.findByRole(role);
    }

    @Cacheable
    @Override
    public List<Permission> findUserPermissions(@NotNull UUID id) {

        Set<Role> memberRoles = roleServices.findUserRoles(id);

        return memberRoles.stream().flatMap(role ->
                findByRoleId(role.getId())
                    .stream()
                    .map(RolePermission::getPermission))
            .toList();
    }
}
