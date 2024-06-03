package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.ParameterExpression;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileReportRepository extends CrudRepository<MemberProfileRecord, UUID> {

    @NonNull
    List<MemberProfileRecord> findAll();

    @Query(value =
            "SELECT id," +
                    "PGP_SYM_DECRYPT(cast(firstname as bytea), :aesKey) as firstName, " +
                    "PGP_SYM_DECRYPT(cast(lastName as bytea), :aesKey) as lastName," +
                    "PGP_SYM_DECRYPT(cast(title as bytea), :aesKey) as title, " +
                    "PGP_SYM_DECRYPT(cast(location as bytea), :aesKey) as location, " +
                    "PGP_SYM_DECRYPT(cast(workEmail as bytea), :aesKey) as workEmail, " +
                    "startDate, " +
                    "PGP_SYM_DECRYPT(cast(pdlfirstname as bytea), :aesKey) as pdlFirstName, " +
                    "PGP_SYM_DECRYPT(cast(pdllastname as bytea), :aesKey) as pdlLastName, " +
                    "PGP_SYM_DECRYPT(cast(pdlemail as bytea), :aesKey) as pdlEmail, " +
                    "PGP_SYM_DECRYPT(cast(supervisorfirstname as bytea), :aesKey) as supervisorFirstName, " +
                    "PGP_SYM_DECRYPT(cast(supervisorlastname as bytea), :aesKey) as supervisorLastName, " +
                    "PGP_SYM_DECRYPT(cast(supervisoremail as bytea), :aesKey) as supervisorEmail " +
            "FROM member_profile_record " +
            "WHERE id IN (:memberIds)")
    @ParameterExpression(name = "aesKey", expression = "#{ env['aes.key'] }")
    List<MemberProfileRecord> findAllByMemberIds(List<String> memberIds);
}
