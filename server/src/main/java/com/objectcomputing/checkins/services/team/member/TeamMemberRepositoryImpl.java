package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.util.Util;
import edu.umd.cs.findbugs.annotations.NonNull;
import nu.studer.sample.tables.pojos.TeamMember;
import org.jooq.DSLContext;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static nu.studer.sample.tables.TeamMember.TEAM_MEMBER;
import static nu.studer.sample.tables.Team.TEAM;
import static nu.studer.sample.tables.MemberProfile.MEMBER_PROFILE;
import static org.jooq.impl.DSL.condition;

@Singleton
public class TeamMemberRepositoryImpl implements TeamMemberRepository {

    private final DSLContext dslContext;

    public TeamMemberRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    @Transactional
    public List<TeamMember> findByTeamid(@NotNull UUID teamid) {
        return dslContext
                .select()
                .from(TEAM_MEMBER)
                .join(TEAM)
                    .on(TEAM.ID.eq(TEAM_MEMBER.TEAM_ID))
                .where(TEAM.ID.eq(teamid.toString()))
                .fetchInto(TeamMember.class);
    }

    @Override
    @Transactional
    public List<TeamMember> findByMemberid(@NotNull UUID memberid) {
        return dslContext
                .select()
                .from(TEAM_MEMBER)
                .join(MEMBER_PROFILE)
                    .on(TEAM.ID.eq(TEAM_MEMBER.TEAM_ID))
                .where(MEMBER_PROFILE.ID.eq(memberid.toString()))
                .fetchInto(TeamMember.class);
    }

    @Override
    @Transactional
    public List<TeamMember> findByLead(Boolean aBoolean) {
        return dslContext
                .select()
                .from(TEAM_MEMBER)
                .where(TEAM_MEMBER.LEAD.eq(aBoolean))
                .fetchInto(TeamMember.class);
    }

    @Override
    @Transactional
    public Optional<TeamMember> findByTeamidAndMemberid(@NotNull UUID teamMemberid, @NotNull UUID memberId) {
        return dslContext
                .select()
                .from(TEAM_MEMBER)
                .where(TEAM_MEMBER.MEMBERID.eq(memberId.toString()))
                .and(TEAM_MEMBER.ID.eq(teamMemberid.toString()))
                .fetchOptionalInto(TeamMember.class);
    }

    @Override
    @Transactional
    public TeamMember save(@NotNull TeamMember entity) {
        return dslContext
                .insertInto(TEAM_MEMBER)
                .columns(TEAM_MEMBER.ID, TEAM_MEMBER.TEAM_ID, TEAM_MEMBER.MEMBERID, TEAM_MEMBER.LEAD)
                .values(UUID.randomUUID().toString(), entity.getTeamId(), entity.getMemberid(), entity.getLead())
                .returningResult().fetchOne().into(TeamMember.class);
    }

    @Override
    @Transactional
    public void deleteByTeamId(@NotNull UUID id) {
        dslContext
                .delete(TEAM_MEMBER)
                .where(TEAM_MEMBER.TEAM_ID.eq(id.toString()));
    }

    @Override
    @Transactional
    public List<TeamMember> search(@Nullable UUID teamId, @Nullable UUID memberId, @Nullable Boolean lead) {
        return dslContext
                .select()
                .from(TEAM_MEMBER)
                .where(condition(teamId == null).or(TEAM_MEMBER.TEAM_ID.eq(nullSafeUUIDToString(teamId))))
                .and(condition(memberId == null).or(TEAM_MEMBER.MEMBERID.eq(nullSafeUUIDToString(memberId))))
                .and(condition(lead == null).or(TEAM_MEMBER.LEAD.eq(lead)))
                .fetchInto(TeamMember.class);
    }
}
