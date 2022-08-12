package com.objectcomputing.checkins.services.memberprofile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MemberProfileServices {

    Optional<MemberProfile> getById(UUID id);

    Set<MemberProfile> findByValues(String firstName, String lastName, String title,
                                    UUID pdlId, String workEmail, UUID supervisorId, Boolean terminated);

    MemberProfile saveProfile(MemberProfile memberProfile);

    Boolean deleteProfile(UUID id);

    MemberProfile findByName(@NotNull String firstName, @NotNull String lastName);

    Optional<MemberProfile> findByWorkEmail(@NotNull String workEmail);

    List<MemberProfile> findAll();
}
