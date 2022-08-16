package com.objectcomputing.checkins.services.referraltype;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReferralTypeRepository extends CrudRepository<ReferralType, UUID> {

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.discoveredOpportunity as bytea),'${aes.key}') as discoveredOpportunity, " +
            "PGP_SYM_DECRYPT(cast(mp.referredBy as bytea),'${aes.key}') as referredBy, " +
            "PGP_SYM_DECRYPT(cast(mp.referrerEmail as bytea),'${aes.key}') as referrerEmail, " +
            "PGP_SYM_DECRYPT(cast(mp.referrerJobSite as bytea),'${aes.key}') as referrerJobSite, " +
            "PGP_SYM_DECRYPT(cast(mp.referrerTypeOther as bytea),'${aes.key}') as referrerTypeOther, " +
            "FROM \"referral_type\" mp " ,
            nativeQuery = true)

    Optional<ReferralType> findByReferrer(@NotNull String referredBy);

    List<ReferralType> findAll();

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.discoveredOpportunity as bytea),'${aes.key}') as discoveredOpportunity, " +
            "PGP_SYM_DECRYPT(cast(mp.referredBy as bytea),'${aes.key}') as referredBy," +
            "PGP_SYM_DECRYPT(cast(mp.referrerEmail as bytea),'${aes.key}') as referrerEmail," +
            "PGP_SYM_DECRYPT(cast(mp.referrerJobSite as bytea),'${aes.key}') as referrerJobSite," +
            "PGP_SYM_DECRYPT(cast(mp.referralTypeOther as bytea),'${aes.key}') as referralTypeOther," +
            "FROM \"referral_type\" mp " +
            "AND  (:discoveredOpportunity IS NULL OR PGP_SYM_DECRYPT(cast(mp.discoveredOpportunity as bytea),'${aes.key}') = :discoveredOpportunity) " +
            "AND  (:referredBy IS NULL OR PGP_SYM_DECRYPT(cast(mp.referredBy as bytea),'${aes.key}') = :referredBy) " +
            "AND  (:referrerEmail IS NULL OR PGP_SYM_DECRYPT(cast(mp.referrerEmail as bytea),'${aes.key}') = :referrerEmail) " +
            "AND  (:referrerJobSite IS NULL OR PGP_SYM_DECRYPT(cast(mp.referrerJobSite as bytea),'${aes.key}') = :referrerJobSite) " +
            "AND  (:referralTypeOther IS NULL OR PGP_SYM_DECRYPT(cast(mp.referralTypeOther as bytea),'${aes.key}') = :referralTypeOther) " ,
            nativeQuery = true)

    List<ReferralType> search(
            @Nullable String discoveredOpportunity,
            @Nullable String referredBy,
            @Nullable String referrerEmail,
            @Nullable String referrerJobSite,
            @Nullable String referralTypeOther);
}
