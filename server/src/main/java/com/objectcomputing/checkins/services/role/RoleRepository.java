package com.objectcomputing.checkins.services.role;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RoleRepository extends CrudRepository<Role, UUID> {

    Optional<Role> findByRole(RoleType role);

    Optional<Role> findById(UUID id);

    @Override
    <S extends Role> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Role> S save(@Valid @NotNull @NonNull S entity);

    @Query(value = "SELECT t_.id, t_.role, t_.description " +
            "FROM role t_ " +
            "LEFT JOIN role_member tm_ " +
            "ON t_.id = tm_.roleid " +
            "WHERE (t_.role = :role) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    Set<Role> search(@Nullable RoleType role, @Nullable String memberid);

}
//
//    SELECT t_.id, t_.role, t_.description
//        FROM role t_ LEFT JOIN role_member tm_
//        ON t_.id = tm_.roleid
//        WHERE ('PDL' IS NULL OR t_.role = 'PDL')
//        AND ('43423423423423423' IS NULL OR tm_.memberid = '43423423423423423')
