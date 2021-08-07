package com.objectcomputing.checkins.services.rale.member;

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
public interface RaleMemberRepository extends CrudRepository<RaleMember, UUID> {

    List<RaleMember> findByRaleId(UUID raleId);

    List<RaleMember> findByMemberId(UUID uuid);

    List<RaleMember> findByLead(Boolean aBoolean);

    Optional<RaleMember> findByRaleIdAndMemberId(@NotNull UUID raleMemberId, @NotNull UUID memberId);

    @Override
    <S extends RaleMember> S save(@Valid @NotNull S entity);

    @Query("DELETE " +
            "FROM rale_member rm_ " +
            "WHERE raleId = :id ")
    void deleteByRaleId(@NotNull String id);

    void deleteByMemberId(@NotNull @NonNull UUID id);

    @Query("SELECT * " +
            "FROM rale_member rm_ " +
            "WHERE (:raleId IS NULL OR rm_.raleId = :raleId) " +
            "AND (:memberId IS NULL OR rm_.memberId = :memberId) " +
            "AND (:lead IS NULL OR rm_.lead = :lead) ")
    List<RaleMember> search(@Nullable String raleId, @Nullable String memberId, @Nullable Boolean lead);
}
