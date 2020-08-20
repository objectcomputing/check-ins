package com.objectcomputing.checkins.services.memberprofile;

import java.util.Set;
import java.util.UUID;

public interface MemberProfileServices {
    MemberProfile getById(UUID id);

    Set<MemberProfile> findByValues(String name, String role, UUID pdlId, String workEmail);

    MemberProfile saveProfile(MemberProfile memberProfile);
}
