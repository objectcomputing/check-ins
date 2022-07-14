package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DemographicsRepository extends CrudRepository<Demographics, UUID> {
    List<Demographics> findAll();

    @Query(value = "SELECT id, " +
            "memberId, gender, degreeLevel, industryTenure, personOfColor, " +
            "veteran, militaryTenure, militaryBranch " +
            "FROM demographics " +
            "WHERE (:memberId IS NULL OR memberId = :memberId) " +
            "AND (:gender IS NULL OR gender = :gender) " +
            "AND (:degreeLevel IS NULL OR degreeLevel = :degreeLevel) " +
            "AND (:industryTenure IS NULL OR industryTenure = :industryTenure) " +
            "AND (:personOfColor IS NULL OR personOfColor = :personOfColor) " +
            "AND (:veteran IS NULL OR veteran = :veteran) " +
            "AND (:militaryTenure IS NULL OR militaryTenure = :militaryTenure) " +
            "AND (:militaryBranch IS NULL OR militaryBranch = :militaryBranch) ", nativeQuery = true)
    List<Demographics> searchByValues(@Nullable String memberId,
                              @Nullable String gender,
                              @Nullable String degreeLevel,
                              @Nullable Integer industryTenure,
                              @Nullable Boolean personOfColor,
                              @Nullable Boolean veteran,
                              @Nullable Integer militaryTenure,
                              @Nullable String militaryBranch);
}
