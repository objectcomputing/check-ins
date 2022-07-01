package com.objectcomputing.checkins.services.onboardeeprofile;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JdbcRepository (dialect = Dialect.POSTGRES)
public interface OnboardingProfileRepository extends CrudRepository<Onboarding_profile, UUID> {
    List<Onboarding_profile> findAll();

    List<Onboarding_profile> search(
            @Nullable UUID id,
            @Nullable String firstName,
            @Nullable String middleName,
            @Nullable String lastName,
            @Nullable Integer socialSecurityNumber,
            @Nullable Date birthDate,
            @Nullable String currentAddress,
            @Nullable String previousAddress,
            @Nullable Integer phoneNumber,
            @Nullable Integer secondPhoneNumber
    );

}
