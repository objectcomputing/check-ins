package com.objectcomputing.checkins.services.demographics;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DemographicsRepository extends CrudRepository<Demographics, UUID> {
    List<Demographics> findAll();

    @Query(value = "", nativeQuery = true)
    List<Demographics> search(@Nullable String memberId,
                              @Nullable String gender,
                              @Nullable String degreeLevel,
                              @Nullable Integer industryTenure,
                              @Nullable Boolean personOfColor,
                              @Nullable Boolean veteran,
                              @Nullable Integer militaryTenure,
                              @Nullable String militaryBranch);
}
