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
        Optional<MemberProfile> memberProfile = memberProfileRepository.findById(id);
        if (memberProfile.isEmpty()) {
            throw new MemberProfileDoesNotExistException("No member profile for id");
        }
        return memberProfile.get();
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
        if(emailProfile != null && emailProfile.getId() != null && !Objects.equals(memberProfile.getId(), emailProfile.getId())) {
            throw new MemberSkillAlreadyExistsException(String.format("Email %s already exists in database",
                    memberProfile.getWorkEmail()));
        }
        if (memberProfile.getId() == null) {
            return memberProfileRepository.save(memberProfile);
        }
        if (memberProfileRepository.findById(memberProfile.getId()) == null) {
            throw new MemberProfileBadArgException("No member profile exists for the ID");
        }
        return memberProfileRepository.update(memberProfile);
    }
}
