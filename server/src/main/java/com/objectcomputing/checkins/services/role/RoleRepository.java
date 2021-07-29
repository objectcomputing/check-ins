package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.services.role.Role;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
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

//    List<Role> findByMemberid(UUID uuid);

//    Optional<Role> findByRole(RoleType role, UUID memberId);

    void deleteByRole(RoleType role);

//    void deleteByRoleAndMemberid(RoleType role, UUID memberId);

    @Override
    <S extends Role> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Role> S save(@Valid @NotNull @NonNull S entity);

    @Query(value = "SELECT t_.id, PGP_SYM_DECRYPT(cast(t_.role as bytea),'${aes.key}') as role, PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description  " +
            "FROM role t_ " +
            "LEFT JOIN role_member tm_ " +
            "   ON t_.id = tm_.roleid " +
            "WHERE (:role IS NULL OR PGP_SYM_DECRYPT(cast(t_.role as bytea),'${aes.key}') = :role) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    List<Role> search(@Nullable RoleType role, @Nullable String memberid);

}
