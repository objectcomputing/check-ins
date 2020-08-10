package com.objectcomputing.checkins.services.role;

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
public interface RoleRepository extends CrudRepository<Role, UUID> {

    List<Role> findByRole(RoleType role);

    List<Role> findByMemberid(UUID uuid);

    Optional<Role> findByRoleAndMemberid(RoleType role, UUID memberId);

    @Override
    <S extends Role> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Role> S save(@Valid @NotNull @NonNull S entity);

}
