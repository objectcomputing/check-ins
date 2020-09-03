package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileRepository extends CrudRepository<MemberProfile, UUID> {
    @Nullable
    Optional<MemberProfile> findById(@NotBlank UUID id);

    Optional<MemberProfile> findByWorkEmail(String workEmail);
    List<MemberProfile> findByName(@NotBlank String name);
    List<MemberProfile> findByRole(@NotBlank String name);
    List<MemberProfile> findByPdlId(@NotBlank UUID pdlId);
    List<MemberProfile> findAll();

}
