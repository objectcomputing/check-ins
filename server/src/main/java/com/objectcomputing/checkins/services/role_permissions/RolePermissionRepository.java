package com.objectcomputing.checkins.services.role_permissions;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RolePermissionRepository extends CrudRepository<RolePermission, UUID> {

    List<RolePermission> findByPermission(RolePermissionType permission);

    List<RolePermission> findByRoleid(UUID uuid);

    Optional<RolePermission> findByPermissionAndRoleid(RolePermissionType permission, UUID memberId);

    @Override
    <S extends RolePermission> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends RolePermission> S save(@Valid @NotNull @NonNull S entity);

}
