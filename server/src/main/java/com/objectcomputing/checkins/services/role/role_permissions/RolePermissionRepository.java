package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RolePermissionRepository extends CrudRepository<RolePermission, RolePermissionId> {

    @Query("INSERT INTO role_permissions " +
            "    (roleid, permission) " +
            "VALUES " +
            "    (:roleid, :permission)")
    void saveByIds(String roleid, Permission permission);

    @Query("SELECT * from role_permissions " +
            "WHERE roleid = :roleid " +
            "AND permission = :permission")
    List<RolePermission> findByIds(String roleid, Permission permission);

    @Query("DELETE FROM role_permissions " +
            "WHERE roleid = :roleid " +
            "AND permission = :permission")
    void deleteByIds(String roleid, Permission permission);

    @NonNull
    List<RolePermission> findAll();

    List<RolePermission> findByRoleId(UUID roleId);
}
