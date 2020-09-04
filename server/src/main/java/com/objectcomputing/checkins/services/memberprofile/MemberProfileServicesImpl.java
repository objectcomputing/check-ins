package com.objectcomputing.checkins.services.memberprofile;


import com.objectcomputing.checkins.services.member_skill.MemberSkillAlreadyExistsException;
import io.micronaut.core.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

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
    public Set<MemberProfile> findByValues(String name, String role, UUID pdlId, String workEmail) {
        Set<MemberProfile> foundProfiles = new HashSet<>(memberProfileRepository.findAll());
        if (!StringUtils.isEmpty(name)) {
            foundProfiles.retainAll(memberProfileRepository.findByName(name));
        }
        if (!StringUtils.isEmpty(role)) {
            foundProfiles.retainAll(memberProfileRepository.findByRole(role));
        }
        if (pdlId != null) {
            foundProfiles.retainAll(memberProfileRepository.findByPdlId(pdlId));
        }
        if (workEmail != null) {
            Optional<MemberProfile> result = memberProfileRepository.findByWorkEmail(workEmail);
            result.ifPresent(memberProfile -> foundProfiles.retainAll(Collections.singleton(memberProfile)));
        }
        return foundProfiles;
    }

    @Override
    public MemberProfile saveProfile(MemberProfile memberProfile) {
        MemberProfile emailProfile = memberProfileRepository.findByWorkEmail(memberProfile.getWorkEmail()).orElse(null);
        if(emailProfile != null && emailProfile.getUuid() != null && !Objects.equals(memberProfile.getUuid(), emailProfile.getUuid())) {
            throw new MemberSkillAlreadyExistsException(String.format("Email %s already exists in database",
                    memberProfile.getWorkEmail()));
        }
        if (memberProfile.getUuid() == null) {
            return memberProfileRepository.save(memberProfile);
        }
        if (memberProfileRepository.findByUuid(memberProfile.getUuid()) == null) {
            throw new MemberProfileBadArgException("No profile exists for this ID");
        }
        return memberProfileRepository.update(memberProfile);
    }
}
