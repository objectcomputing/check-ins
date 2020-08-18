package com.objectcomputing.checkins.services.memberprofile;

import java.util.List;
import java.util.UUID;

public interface MemberProfileServices {
    MemberProfile getById(UUID id);

    List<MemberProfile> findByValues(String name, String role, UUID pdlId);

    MemberProfile saveProfile(MemberProfile memberProfile);
}
