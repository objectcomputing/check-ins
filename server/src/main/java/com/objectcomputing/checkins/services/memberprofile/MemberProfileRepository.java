package com.objectcomputing.checkins.services.memberprofile;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileRepository extends CrudRepository<MemberProfile, UUID> {
    @Nullable
    MemberProfile findByUuid(@NotBlank UUID uuid);
    
    List<MemberProfile> findByName(@NotBlank String name);
    List<MemberProfile> findByRole(@NotBlank String name);
    List<MemberProfile> findByPdlId(@NotBlank UUID pdlId);
    List<MemberProfile> findAll();


    @Query(" SELECT * " +
            "FROM member_profile mp " +
            "WHERE (:name IS NULL OR mp.name = :name) " +
            "AND (:role IS NULL OR mp.role = :role) " +
            "AND (:pdlId IS NULL OR mp.pdlid = :pdlId)")
    List<MemberProfile> search(@Nullable String name, @Nullable String role, @Nullable String pdlId);
}
