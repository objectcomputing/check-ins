package com.objectcomputing.checkins.services.checkins;

import java.util.List;
import java.util.UUID;

import com.objectcomputing.checkins.services.checkins.CheckIn;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckInRepository extends CrudRepository<CheckIn,UUID>{

    List<CheckIn> findByTeamMemberId(UUID teamMemberId);
    List<CheckIn> findByPdlId(UUID pdlId);
    List<CheckIn> findByTargetYearAndTargetQtr(String targetYear,String targetQtr);
    List<CheckIn> findAll();

}