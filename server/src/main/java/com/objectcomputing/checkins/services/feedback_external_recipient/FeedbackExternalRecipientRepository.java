package com.objectcomputing.checkins.services.feedback_external_recipient;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.DataType;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackExternalRecipientRepository extends CrudRepository<FeedbackExternalRecipient, UUID> {

    @Override
    <S extends FeedbackExternalRecipient> S save(@Valid @NotNull @NonNull S entity);
    @Override
    <S extends FeedbackExternalRecipient> S update(@NotNull @NonNull S entity);

    // , PGP_SYM_DECRYPT(cast(FER. as bytea), '${aes.key}') as

    @Query("" +
            "SELECT id" +
            ", PGP_SYM_DECRYPT(cast(FER.email as bytea), '${aes.key}') as email" +
            ", PGP_SYM_DECRYPT(cast(FER.firstname as bytea), '${aes.key}') as firstname" +
            ", PGP_SYM_DECRYPT(cast(FER.lastname as bytea), '${aes.key}') as lastname" +
            ", PGP_SYM_DECRYPT(cast(FER.company_name as bytea), '${aes.key}') as company_name" +
            ", FER.inactive " +
            "FROM feedback_external_recipient FER " +
            "WHERE 1=1 " +
            "AND (:email IS NULL OR FER.email = :email) " +
            "AND (:firstName IS NULL OR FER.firstName = :firstName) " +
            "AND (:lastName IS NULL OR FER.lastName = :lastName) " +
            "AND (:companyName IS NULL OR FER.company_name = :companyName) " +
            "AND (:inactive IS NULL OR coalesce(FER.inactive,false) = :inactive)"
    )
    List<FeedbackExternalRecipient> findByValues(
            @Nullable String email, @Nullable String firstName, @Nullable String lastName, @Nullable String companyName, @Nullable Boolean inactive
    );

}

