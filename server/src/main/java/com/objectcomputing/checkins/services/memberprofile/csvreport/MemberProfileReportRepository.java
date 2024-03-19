package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileReportRepository extends CrudRepository<MemberProfileRecord, UUID> {

    @NonNull
    List<MemberProfileRecord> findAll();

    List<MemberProfileRecord> findByIdInList(List<UUID> memberIds);

}
