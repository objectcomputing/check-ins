package com.objectcomputing.checkins.services.memberprofile;

import java.util.Set;
import java.util.UUID;

public interface MemberProfileServices {
    MemberProfileEntity getById(UUID id);

    Set<MemberProfileEntity> findByValues(String name, String title, UUID pdlId, String workEmail);

    MemberProfileEntity saveProfile(MemberProfileEntity memberProfileEntity);
}
