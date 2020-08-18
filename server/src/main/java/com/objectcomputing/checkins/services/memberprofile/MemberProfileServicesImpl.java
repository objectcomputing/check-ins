package com.objectcomputing.checkins.services.memberprofile;


import io.micronaut.core.util.StringUtils;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MemberProfileServicesImpl implements MemberProfileServices {

    @Inject
    private MemberProfileRepository memberProfileRepository;

    @Override
    public MemberProfile getById(UUID id) {
        MemberProfile memberProfile = memberProfileRepository.findByUuid(id);
        if (memberProfile == null) {
            throw new MemberProfileDoesNotExistException("No member profile for id");
        }
        return memberProfile;
    }

    @Override
    public Set<MemberProfile> findByValues(String name, String role, UUID pdlId) {
        /*Set<MemberProfile> foundProfiles = new HashSet<>(memberProfileRepository.findAll());
        if (!StringUtils.isEmpty(name)) {
            foundProfiles.retainAll(memberProfileRepository.findByName(name));
        }
        if (!StringUtils.isEmpty(role)) {
            foundProfiles.retainAll(memberProfileRepository.findByRole(role));
        }
        if (pdlId != null) {
            foundProfiles.retainAll(memberProfileRepository.findByPdlId(pdlId));
        }
        return foundProfiles;*/
        return new HashSet<>(
                memberProfileRepository.search(name, role, (pdlId == null ? null : pdlId.toString()))
        );
    }

    @Override
    public MemberProfile saveProfile(MemberProfile memberProfile) {
        if (memberProfile.getUuid() == null) {
            return memberProfileRepository.save(memberProfile);
        }
        if (memberProfileRepository.findByUuid(memberProfile.getUuid()) == null) {
            throw new MemberProfileBadArgException("No profile exists for this ID");
        }
        return memberProfileRepository.update(memberProfile);
    }
}
