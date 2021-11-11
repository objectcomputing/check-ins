package com.objectcomputing.checkins.services.role;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RoleRepository extends CrudRepository<Role, UUID> {

    @Query("SELECT DISTINCT role.id, role.role,  PGP_SYM_DECRYPT(cast(role.description as bytea), '${aes.key}') as description  " +
            "from role " +
            "WHERE LOWER(role.role) = LOWER(:role)")
    Optional<Role> findByRole(String role);

    @Query("SELECT DISTINCT role.id, role.role,  PGP_SYM_DECRYPT(cast(role.description as bytea), '${aes.key}') as description  " +
            "FROM member_profile " +
            "JOIN member_roles " +
            "    ON member_profile.id = member_roles.memberid " +
            "JOIN role " +
            "    ON role.id = member_roles.roleid " +
            "WHERE member_profile.id = :id")
    Set<Role> findUserRoles(UUID id);


    @Override
    <S extends Role> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Role> S save(@Valid @NotNull @NonNull S entity);

}
