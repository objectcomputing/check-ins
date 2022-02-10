package com.objectcomputing.checkins.gcp.chat;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GoogleChatBotEntryRepo extends CrudRepository<GoogleChatBotEntry, UUID> {

    GoogleChatBotEntry findByMemberId(UUID memberId);

    @Override
    <S extends GoogleChatBotEntry> S save(@NotNull @Valid S entity);

}