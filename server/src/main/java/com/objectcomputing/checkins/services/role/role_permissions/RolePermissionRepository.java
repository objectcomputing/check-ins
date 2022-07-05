package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RolePermissionRepository extends CrudRepository<RolePermission, RolePermissionId> {

    @Query("INSERT INTO role_permissions " +
            "    (roleid, permissionid) " +
            "VALUES " +
            "    (:roleId, :permissionId)")
    RolePermission saveByIds(UUID roleId, UUID permissionId);

    Optional<RolePermission> findByRoleIdAndPermissionId(@NotNull UUID roleId, @NotNull UUID permissionId);

    @NonNull
    List<RolePermission> findAll();
}
