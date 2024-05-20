package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GuildMemberHistoryRepository extends CrudRepository<GuildMemberHistory, UUID> {

    void deleteByGuildId(@NotNull UUID guildId);
}
