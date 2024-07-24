package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface KudosRecipientRepository extends CrudRepository<KudosRecipient, UUID> {

    List<KudosRecipient> findByKudosId(@NotNull UUID kudosId);

    List<KudosRecipient> findByMemberId(@NotNull UUID memberId);

}
