package com.objectcomputing.checkins.services.guild.member;

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
public interface GuildMemberRepository extends CrudRepository<GuildMember, UUID> {

    List<GuildMember> findByGuildid(UUID guildid);

    List<GuildMember> findByMemberid(UUID uuid);

    List<GuildMember> findByLead(Boolean aBoolean);

    Optional<GuildMember> findByGuildidAndMemberid(UUID guildMemberid, UUID memberId);

    @Override
    <S extends GuildMember> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends GuildMember> S save(@Valid @NotNull @NonNull S entity);

}
