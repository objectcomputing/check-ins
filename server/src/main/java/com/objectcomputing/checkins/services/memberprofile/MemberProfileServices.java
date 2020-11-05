package com.objectcomputing.checkins.services.memberprofile;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public interface MemberProfileServices {
    MemberProfileEntity getById(UUID id);

    Set<MemberProfileEntity> findByValues(String name, String title, UUID pdlId, String workEmail);

    MemberProfileEntity saveProfile(MemberProfileEntity memberProfileEntity);

    MemberProfileEntity findByName(@NotNull String name);
}
