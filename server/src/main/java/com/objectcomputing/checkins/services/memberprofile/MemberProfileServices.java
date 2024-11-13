package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.cache.annotation.Cacheable;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MemberProfileServices {
    MemberProfile getById(UUID id);

    @Cacheable
    MemberProfile findByWorkEmail(@NotNull String workEmail);

    Set<MemberProfile> findByValues(String firstName, String lastName, String title,
                                    UUID pdlId, String workEmail, UUID supervisorId, Boolean terminated);

    MemberProfile saveProfile(MemberProfile memberProfile);

    boolean deleteProfile(UUID id);

    MemberProfile findByName(@NotNull String firstName, @NotNull String lastName);

    List<MemberProfile> findAll();

    List<MemberProfile> getSupervisorsForId(UUID id);

    List<MemberProfile> getSubordinatesForId(UUID id);

    MemberProfile updateProfile(MemberProfile memberProfile);
}
