package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

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
            "PGP_SYM_DECRYPT(cast(location as bytea), '${aes.key}') as location, " +
            "PGP_SYM_DECRYPT(cast(workEmail as bytea), '${aes.key}') as workEmail, " +
            "employeeId, startDate, " +
            "PGP_SYM_DECRYPT(cast(bioText as bytea), '${aes.key}') as bioText, " +
            "supervisorid, terminationDate, birthDate, voluntary, excluded, last_seen " +
            "FROM \"member_profile\" mp " +
            "WHERE  (:workEmail IS NULL OR PGP_SYM_DECRYPT(cast(mp.workEmail as bytea), '${aes.key}') = :workEmail) ",
            nativeQuery = true)
    Optional<MemberProfile> findByWorkEmail(@NotNull String workEmail);

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
            "PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName, " +
            "PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName, " +
            "PGP_SYM_DECRYPT(cast(mp.suffix as bytea),'${aes.key}') as suffix, " +
            "PGP_SYM_DECRYPT(cast(title as bytea),'${aes.key}') as title, " +
            "pdlid, " +
            "PGP_SYM_DECRYPT(cast(location as bytea), '${aes.key}') as location, " +
            "PGP_SYM_DECRYPT(cast(workEmail as bytea), '${aes.key}') as workEmail, " +
            "employeeId, startDate, " +
            "PGP_SYM_DECRYPT(cast(bioText as bytea), '${aes.key}') as bioText, " +
            "supervisorid, terminationDate, birthDate, voluntary, excluded, last_seen " +
            "FROM \"member_profile\" mp " +
            "WHERE (:firstName IS NULL OR PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') = :firstName) " +
            "AND (:middleName IS NULL OR PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') = :middleName) " +
            "AND (:lastName IS NULL OR PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') = :lastName) " +
            "AND (:suffix IS NULL OR PGP_SYM_DECRYPT(cast(mp.suffix as bytea),'${aes.key}') = :suffix) " +
            "AND (:title IS NULL OR PGP_SYM_DECRYPT(cast(mp.title as bytea), '${aes.key}') = :title) " +
            "AND (:pdlId IS NULL OR mp.pdlId = :pdlId) " +
            "AND (:workEmail IS NULL OR PGP_SYM_DECRYPT(cast(mp.workEmail as bytea), '${aes.key}') = :workEmail) " +
            "AND (:supervisorId IS NULL OR mp.supervisorId = :supervisorId) " +
            "AND (((:terminated IS FALSE OR :terminated IS NULL) AND (mp.terminationdate IS NULL OR mp.terminationdate >= CURRENT_DATE)) " +
            "OR (:terminated IS TRUE AND mp.terminationdate < CURRENT_DATE))", nativeQuery = true )
    List<MemberProfile> search(@Nullable String firstName, @Nullable String middleName, @Nullable String lastName,
                               @Nullable String suffix, @Nullable String title, @Nullable String pdlId,
                               @Nullable String workEmail, @Nullable String supervisorId, @Nullable Boolean terminated);

    List<MemberProfile> findAll();

    @Query(value = "WITH RECURSIVE subordinate AS (SELECT " +
    "id, firstname, middlename, lastname, suffix, title, pdlid, location, workemail, employeeid, startdate, biotext, supervisorid, terminationdate, birthdate, voluntary, excluded, last_seen, 0 as level " +
    "FROM member_profile " +
    "WHERE id = :id and terminationdate is NULL " +
    "   UNION ALL " +
    "SELECT " +
    "e.id, e.firstname, e.middlename, e.lastname, e.suffix, e.title, e.pdlid, e.location, e.workemail, e.employeeid, e.startdate, e.biotext, e.supervisorid, e.terminationdate, e.birthdate, e.voluntary, e.excluded, e.last_seen, level + 1 " +
    "FROM member_profile e " +
    "JOIN subordinate s " +
    "ON s.supervisorid = e.id " +
    "WHERE e.terminationdate is NULL " +
    ") " +
    "SELECT " +
    "DISTINCT " +
    "s.id AS id, " +
    "PGP_SYM_DECRYPT(cast(s.firstname as bytea),'${aes.key}') as firstname, " +
    "PGP_SYM_DECRYPT(cast(s.middlename as bytea),'${aes.key}') as middlename, " +
    "PGP_SYM_DECRYPT(cast(s.lastname as bytea),'${aes.key}') as lastname, " +
    "PGP_SYM_DECRYPT(cast(s.suffix as bytea),'${aes.key}') as suffix, " +
    "PGP_SYM_DECRYPT(cast(s.title as bytea),'${aes.key}') as title, " +
    "s.pdlid, " +
    "PGP_SYM_DECRYPT(cast(s.location as bytea), '${aes.key}') as location, " +
    "PGP_SYM_DECRYPT(cast(s.workemail as bytea), '${aes.key}') as workemail, " +
    "s.employeeid, s.startdate, " +
    "PGP_SYM_DECRYPT(cast(s.biotext as bytea), '${aes.key}') as biotext, " +
    "s.supervisorid, s.terminationdate, s.birthdate, s.voluntary, s.excluded, s.last_seen, " +
    "s.level " +
    "FROM subordinate s " +
    "WHERE s.id <> :id " +
    "ORDER BY level", nativeQuery = true)
    List<MemberProfile> findSupervisorsForId(UUID id);
}
