package com.objectcomputing.checkins.services.permissions;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PermissionRepository extends CrudRepository<Permission, UUID> {

    @Query("SELECT DISTINCT permissions.id, permissions.permission, permissions.description " +
            "FROM member_profile " +
            "JOIN member_roles " +
            "    ON member_profile.id = member_roles.memberid " +
            "JOIN role " +
            "    ON role.id = member_roles.roleid " +
            "JOIN role_permissions " +
            "    ON role.id = role_permissions.roleid " +
            "JOIN permissions " +
            "    ON permissions.id = role_permissions.permissionid " +
            "WHERE member_profile.id = :id")
    List<Permission> findUserPermissions(UUID id);
    
    @NonNull
    List<Permission> findAll();

    List<Permission> listOrderByPermission();
}
