package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileRepository extends CrudRepository<MemberProfile, UUID> {

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
            "PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName," +
            "PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName," +
            "PGP_SYM_DECRYPT(cast(mp.suffix as bytea),'${aes.key}') as suffix," +
            "PGP_SYM_DECRYPT(cast(title as bytea),'${aes.key}') as title, " +
            "pdlid, " +
            "PGP_SYM_DECRYPT(cast(location as bytea),'${aes.key}') as location, " +
            "PGP_SYM_DECRYPT(cast(workEmail as bytea),'${aes.key}') as workEmail, " +
            "employeeId, startDate, " +
            "PGP_SYM_DECRYPT(cast(bioText as bytea),'${aes.key}') as bioText, " +
            "supervisorid, terminationDate " +
            "FROM member_profile mp " +
            "WHERE  (:workEmail IS NULL OR PGP_SYM_DECRYPT(cast(mp.workEmail as bytea), '${aes.key}') = :workEmail) ",
             nativeQuery = true )
    Optional<MemberProfile> findByWorkEmail(@NotNull String workEmail);

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
            "PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName, " +
            "PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName, " +
            "PGP_SYM_DECRYPT(cast(mp.suffix as bytea),'${aes.key}') as suffix, " +
            "PGP_SYM_DECRYPT(cast(title as bytea),'${aes.key}') as title, " +
            "pdlid, " +
            "PGP_SYM_DECRYPT(cast(location as bytea),'${aes.key}') as location, " +
            "PGP_SYM_DECRYPT(cast(workEmail as bytea),'${aes.key}') as workEmail, " +
            "employeeId, startDate, " +
            "PGP_SYM_DECRYPT(cast(bioText as bytea),'${aes.key}') as bioText, " +
            "supervisorid, terminationDate " +
            "FROM member_profile mp " +
            "WHERE (:firstName IS NULL OR PGP_SYM_DECRYPT(cast(mp.firstName as bytea), '${aes.key}') = :firstName) " +
            "AND (:middleName IS NULL OR PGP_SYM_DECRYPT(cast(mp.middleName as bytea), '${aes.key}') = :middleName) " +
            "AND (:lastName IS NULL OR PGP_SYM_DECRYPT(cast(mp.lastName as bytea), '${aes.key}') = :lastName) " +
            "AND (:suffix IS NULL OR PGP_SYM_DECRYPT(cast(mp.suffix as bytea), '${aes.key}') = :suffix) " +
            "AND (:title IS NULL OR PGP_SYM_DECRYPT(cast(mp.title as bytea), '${aes.key}') = :title) " +
            "AND (:pdlId IS NULL OR mp.pdlId = :pdlId) " +
            "AND (:workEmail IS NULL OR PGP_SYM_DECRYPT(cast(mp.workEmail as bytea), '${aes.key}') = :workEmail) " +
            "AND (:supervisorId IS NULL OR mp.supervisorId = :supervisorId) ", nativeQuery = true )
    List<MemberProfile> search(@Nullable String firstName, @Nullable String middleName, @Nullable String lastName,
                               @Nullable String suffix, @Nullable String title, @Nullable String pdlId,
                               @Nullable String workEmail, @Nullable String supervisorId);

    List<MemberProfile> findAll();

    String findNameById(@NotNull UUID id);
}
