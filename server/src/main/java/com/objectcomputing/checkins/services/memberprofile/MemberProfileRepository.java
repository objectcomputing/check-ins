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

    Optional<MemberProfile> findByWorkEmail(@NotNull String workEmail);

    @Query(value = "SELECT * " +
            "FROM member_profile mp " +
            "WHERE (:name IS NULL OR mp.name = :name) " +
            "AND (:title IS NULL OR mp.title = :title) " +
            "AND (:pdlId IS NULL OR mp.pdlId = :pdlId) " +
            "AND (:workEmail IS NULL OR mp.workEmail = :workEmail) " +
            "AND (:supervisorId IS NULL OR mp.supervisorId = :supervisorId) ", nativeQuery = true )
    List<MemberProfile> search(@Nullable String name, @Nullable String title, @Nullable String pdlId, @Nullable String workEmail, @Nullable String supervisorId);

    List<MemberProfile> findAll();

    String findNameById(@NotNull UUID id);
}
