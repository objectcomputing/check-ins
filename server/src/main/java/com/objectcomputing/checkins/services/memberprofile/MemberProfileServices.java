package com.objectcomputing.checkins.services.memberprofile;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public interface MemberProfileServices {
    MemberProfile getById(UUID id);

    Set<MemberProfile> findByValues(String name, String title, UUID pdlId, String workEmail, UUID supervisorId);

    MemberProfile saveProfile(MemberProfile memberProfile);

    Boolean deleteProfile(UUID id);

    MemberProfile findByName(@NotNull String name);
}
