package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponse;
// import com.objectcomputing.checkins.services.role.Role;
// import com.objectcomputing.checkins.services.role.RoleType;

import java.util.UUID;

public interface PulseResponseFixture extends RepositoryFixture {
    default PulseResponse createDefaultPulseResponseRepository(MemberProfile memberProfile) {
        return createDefaultPulseResponseRepository(memberProfile);
    }

    // default PulseResponse createDefaultPulseResponseRepository(MemberProfile memberProfile) {
    //     return getPulseResponseRepository().save(memberProfile.getUuid());
    // }

    // default PulseResponse findRole(Role role) {
    //     return findRoleById(role.getId());
    // }

    default PulseResponse findByTeamMemberId(UUID uuid) {
        return getPulseResponseRepository().findById(uuid).orElse(null);
    }

}
