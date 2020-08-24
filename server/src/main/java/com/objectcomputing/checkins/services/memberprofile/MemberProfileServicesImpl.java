package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.memberSkill.MemberSkillAlreadyExistsException;

import javax.inject.Inject;
import java.util.Objects;
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
    public Set<MemberProfile> findByValues(String name, String role, String pdlId, String workEmail) {
        return memberProfileRepository
                .search(name, role, pdlId, workEmail);
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
