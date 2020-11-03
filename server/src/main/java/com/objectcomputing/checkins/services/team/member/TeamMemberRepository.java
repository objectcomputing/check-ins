package com.objectcomputing.checkins.services.team.member;

import nu.studer.sample.tables.pojos.TeamMember;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamMemberRepository {//} extends CrudRepository<TeamMemberEntity, UUID> {

    List<TeamMember> findByTeamid(UUID teamid);

    List<TeamMember> findByMemberid(UUID uuid);

    List<TeamMember> findByLead(Boolean aBoolean);

    Optional<TeamMember> findByTeamidAndMemberid(@NotNull UUID teamMemberid, @NotNull UUID memberId);

    TeamMember save(@NotNull TeamMember entity);

    void deleteByTeamId(@NotNull UUID id);

    List<TeamMember> search(@Nullable UUID teamId, @Nullable UUID memberId, @Nullable Boolean lead);
}
