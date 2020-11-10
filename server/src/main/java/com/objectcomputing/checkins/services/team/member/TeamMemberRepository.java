package com.objectcomputing.checkins.services.team.member;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamMemberRepository extends CrudRepository<TeamMember, UUID> {

    List<TeamMember> findByTeamid(@NotNull UUID teamid);

    List<TeamMember> findByMemberid(@NotNull UUID memberid);

    List<TeamMember> findByLead(@NotNull Boolean aBoolean);

    List<TeamMember> findBySupervisorid(@NotNull UUID supervisorid);

    Optional<TeamMember> findByTeamidAndMemberid(UUID teamMemberid, UUID memberId);

    @Override
    <S extends TeamMember> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends TeamMember> S save(@Valid @NotNull @NotNull S entity);

}
