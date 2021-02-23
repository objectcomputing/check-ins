package com.objectcomputing.checkins.services.guild.member;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GuildMemberRepository extends CrudRepository<GuildMember, UUID> {

    List<GuildMember> findByMemberid(UUID uuid);

    @Query("SELECT * " +
            "FROM guildmembers guildMember " +
            "WHERE (:guildid IS NULL OR guildMember.guildid = :guildid) " +
            "AND (:memberid IS NULL OR guildMember.memberid = :memberid)" +
            "AND (:lead IS NULL OR guildMember.lead = :lead)")
    Set<GuildMember> search(@Nullable String guildid, @Nullable String memberid, @Nullable Boolean lead);

    @Override
    <S extends GuildMember> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends GuildMember> S save(@Valid @NotNull @NonNull S entity);

}
