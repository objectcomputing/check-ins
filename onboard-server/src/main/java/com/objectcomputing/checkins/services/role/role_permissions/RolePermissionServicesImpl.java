package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionRepository;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionServicesImpl(RolePermissionRepository rolePermissionRepository,
                                      RoleServices roleServices,
                                      PermissionServices permissionServices,
                                      RoleRepository roleRepository,
                                      PermissionRepository permissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleServices = roleServices;
        this.permissionServices = permissionServices;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RolePermission saveByIds(UUID roleId, UUID permissionId) {

        // Ensure the role exists
        roleRepository.findById(roleId).orElseThrow(() -> {
            throw new BadArgException(String.format("Attempted to save role permission where role %s does not exist", roleId));
        });

        // Ensure the permission exists
        permissionRepository.findById(permissionId).orElseThrow(() -> {
            throw new BadArgException(String.format("Attempted to save role permission where permission %s does not exist", permissionId));
        });

        // Ensure this role has not already been granted this permission
        rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId).ifPresent(rp -> {
            throw new BadArgException(String.format("Attempted to save role permission where role %s already has permission %s", roleId, permissionId));
        });

        return rolePermissionRepository.save(new RolePermission(roleId, permissionId));
    }

    @Override
    public boolean delete(RolePermissionId id) {
        rolePermissionRepository.findByRoleIdAndPermissionId(id.getRoleId(), id.getPermissionId()).orElseThrow(() -> {
            throw new BadArgException("Attempted to delete role permission with invalid id");
        });
        rolePermissionRepository.deleteById(id);
        return true;
    }

    public List<RolePermissionResponseDTO> findAll() {
        List<RolePermissionResponseDTO> roleInfo = new ArrayList<>();
        List<RolePermission> records =  rolePermissionRepository.findAll();
        List<Role> roles = roleServices.findAllRoles();
        List<Permission> permissions = permissionServices.findAll();

        for(Role role : roles) {
            List<Permission> permissionsAssociatedWithRole = new ArrayList<>();
            for(RolePermission rolePermission : records) {
                if(role.getId().equals(rolePermission.getRolePermissionId().getRoleId())) {
                    Optional<Permission> permission = permissions.stream().filter(s-> s.getId().equals(rolePermission.getRolePermissionId().getPermissionId())).findFirst();
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
