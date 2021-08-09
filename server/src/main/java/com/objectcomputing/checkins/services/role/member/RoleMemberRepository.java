package com.objectcomputing.checkins.services.role.member;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RoleMemberRepository extends CrudRepository<RoleMember, UUID> {

    List<RoleMember> findByRoleId(UUID roleId);

    List<RoleMember> findByMemberId(UUID uuid);

    List<RoleMember> findByLead(Boolean aBoolean);

    Optional<RoleMember> findByRoleIdAndMemberId(@NotNull UUID roleMemberId, @NotNull UUID memberId);

    @Override
    <S extends RoleMember> S save(@Valid @NotNull S entity);

    @Query("DELETE " +
            "FROM role_member rm_ " +
            "WHERE roleId = :id ")
    void deleteByRoleId(@NotNull String id);

    void deleteByMemberId(@NotNull @NonNull UUID id);

    @Query("SELECT * " +
            "FROM role_member rm_ " +
            "WHERE (:roleId IS NULL OR rm_.roleId = :roleId) " +
            "AND (:memberId IS NULL OR rm_.memberId = :memberId) " +
            "AND (:lead IS NULL OR rm_.lead = :lead) ")
    List<RoleMember> search(@Nullable String roleId, @Nullable String memberId, @Nullable Boolean lead);
}
