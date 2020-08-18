package com.objectcomputing.checkins.services.memberprofile;


import javax.inject.Inject;
import java.util.List;
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
    public List<MemberProfile> findByValues(String name, String role, UUID pdlId) {
        return memberProfileRepository
                .search(name, role, (pdlId == null ? null : pdlId.toString()));
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
