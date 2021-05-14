package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GuildMemberRepository extends CrudRepository<GuildMember, UUID> {

    List<GuildMember> findByGuildid(UUID guildid);

    List<GuildMember> findByMemberid(UUID uuid);

    List<GuildMember> findByLead(Boolean aBoolean);

    Optional<GuildMember> findByGuildidAndMemberid(@NotNull UUID guildMemberid, @NotNull UUID memberId);

    GuildMember save(@NotNull GuildMember entity);

    @Query("DELETE " +
            "FROM guild_member tm_ " +
            "WHERE guildid = :id ")
    void deleteByGuildId(@NotNull String id);

    void deleteByMemberid(@NotNull @Nonnull UUID id);

    @Query("SELECT * " +
            "FROM guild_member tm_ " +
            "WHERE (:guildId IS NULL OR tm_.guildid = :guildId) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) " +
            "AND (:lead IS NULL OR tm_.lead = :lead) ")
    List<GuildMember> search(@Nullable String guildId, @Nullable String memberid, @Nullable Boolean lead);
}
