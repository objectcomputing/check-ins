package com.objectcomputing.checkins.services.team.member;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamMemberRepository extends CrudRepository<TeamMember, UUID> {

    List<TeamMember> findByTeamId(UUID teamId);

    List<TeamMember> findByMemberId(UUID uuid);

    List<TeamMember> findByLead(Boolean aBoolean);

    Optional<TeamMember> findByTeamIdAndMemberId(@NotNull UUID teamMemberId, @NotNull UUID memberId);

    @Override
    <S extends TeamMember> S save(@Valid @NotNull S entity);

    @Query("DELETE " +
            "FROM team_member tm_ " +
            "WHERE teamId = :id ")
    void deleteByTeamId(@NotNull String id);

    void deleteByMemberId(@NotNull @NonNull UUID id);

    @Query("SELECT * " +
            "FROM team_member tm_ " +
            "WHERE (:teamId IS NULL OR tm_.teamId = :teamId) " +
            "AND (:memberId IS NULL OR tm_.memberId = :memberId) " +
            "AND (:lead IS NULL OR tm_.lead = :lead) ")
    List<TeamMember> search(@Nullable String teamId, @Nullable String memberId, @Nullable Boolean lead);
}
