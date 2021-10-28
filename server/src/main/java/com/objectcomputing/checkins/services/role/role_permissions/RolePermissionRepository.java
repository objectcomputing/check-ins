package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RolePermissionRepository extends CrudRepository<RolePermission, RolePermissionId> {

    @Query("INSERT INTO role_permissions " +
            "    (roleid, permissionid) " +
            "VALUES " +
            "    (:roleid, :permissionid)")
    RolePermission saveByIds(UUID roleid, UUID permissionid);
}
