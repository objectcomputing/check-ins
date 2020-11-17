package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class MemberProfileServicesImpl implements MemberProfileServices {

    private final MemberProfileRepository memberProfileRepository;

    public MemberProfileServicesImpl(MemberProfileRepository memberProfileRepository) {
        this.memberProfileRepository = memberProfileRepository;
    }

    @Override
    public MemberProfile getById(UUID id) {
        Optional<MemberProfile> memberProfile = memberProfileRepository.findById(id);
        if (memberProfile.isEmpty()) {
            throw new NotFoundException("No member profile for id");
        }
        return memberProfile.get();
    }

    @Override
    public Set<MemberProfile> findByValues(@Nullable String name, @Nullable String title, @Nullable UUID pdlId, @Nullable String workEmail) {
        Set<MemberProfile> memberProfiles = new HashSet<>(
                memberProfileRepository.search(name, title, nullSafeUUIDToString(pdlId), workEmail));

        return memberProfiles;
    }

    @Override
    public MemberProfile saveProfile(MemberProfile memberProfile) {
        MemberProfile emailProfile = memberProfileRepository.findByWorkEmail(memberProfile.getWorkEmail()).orElse(null);
        if(emailProfile != null && emailProfile.getId() != null && !Objects.equals(memberProfile.getId(), emailProfile.getId())) {
            throw new MemberProfileAlreadyExistsException(String.format("Email %s already exists in database",
                    memberProfile.getWorkEmail()));
        }

        if (memberProfile.getId() == null) {
            return memberProfileRepository.save(memberProfile);
        }

        return memberProfileRepository.update(memberProfile);
    }
}
