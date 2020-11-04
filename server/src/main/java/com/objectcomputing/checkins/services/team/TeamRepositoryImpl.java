package com.objectcomputing.checkins.services.team;

import nu.studer.sample.tables.pojos.Team;
import org.jooq.DSLContext;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static nu.studer.sample.tables.Team.TEAM;
import static nu.studer.sample.tables.TeamMember.TEAM_MEMBER;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.condition;

@Singleton
public class TeamRepositoryImpl implements TeamRepository {

    private final DSLContext dslContext;

    public TeamRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    @Transactional
    public Optional<Team> findById(@NotNull UUID id) {
        return dslContext
                .select()
                .from(TEAM)
                .where(TEAM.ID.eq(id.toString()))
                .fetchOptionalInto(Team.class);
    }

    @Override
    @Transactional
    public Optional<Team> findByName(@NotNull String name) {
        return dslContext
                .select()
                .from(TEAM)
                .where(TEAM.NAME.likeIgnoreCase("%"+name+"%"))
                .fetchOptionalInto(Team.class);
    }

    @Override
    @Transactional
    public Team save(@NotNull Team saveMe) {
        UUID insertId = UUID.randomUUID();
        return dslContext
                .insertInto(TEAM)
                .columns(TEAM.ID, TEAM.NAME, TEAM.DESCRIPTION)
                .values(insertId.toString(), saveMe.getName(), saveMe.getDescription())
                .returningResult(asterisk()).fetchOne().into(Team.class);
    }

    @Override
    @Transactional
    public Team update(@NotNull Team updateMe) {
        return dslContext
                .update(TEAM)
                .set(TEAM.NAME, updateMe.getName())
                .set(TEAM.DESCRIPTION, updateMe.getDescription())
                .where(TEAM.ID.eq(updateMe.getId()))
                .returningResult(asterisk()).fetchOne().into(Team.class);
    }

    @Override
    @Transactional
    public List<Team> search(@Nullable String name, @Nullable UUID memberId) {
        return dslContext
                .select(TEAM.ID, TEAM.NAME, TEAM.DESCRIPTION)
                .from(TEAM)
                .leftJoin(TEAM_MEMBER)
                    .on(TEAM.ID.eq(TEAM_MEMBER.TEAMID))
                .where(condition(name == null).or(TEAM.NAME.likeIgnoreCase("%" + name + "%")))
                .or(condition(memberId == null).or(TEAM_MEMBER.MEMBERID.eq(nullSafeUUIDToString(memberId))))
                .fetchInto(Team.class);
    }

    @Override
    @Transactional
    public void deleteById(@NotNull UUID id) {
        dslContext
                .delete(TEAM)
                .where(TEAM.ID.eq(id.toString()));
    }

}
