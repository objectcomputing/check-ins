package com.objectcomputing.checkins.services.memberprofile;

import java.util.List;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileRepository extends CrudRepository<MemberProfile, UUID> {
    List<MemberProfile> findByName(String name);
    List<MemberProfile> findByRole(String name);
    List<MemberProfile> findByPdlId(UUID pdlId);
    List<MemberProfile> findAll();
}
