package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.member_skill.MemberSkillAlreadyExistsException;

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
    public Set<MemberProfile> findByValues(@Nullable String name,
                                           @Nullable String title,
                                           @Nullable UUID pdlId,
                                           @Nullable String workEmail,
                                           @Nullable UUID supervisorId) {
        return new HashSet<>(memberProfileRepository.search(name, title, nullSafeUUIDToString(pdlId),
                                                            workEmail, nullSafeUUIDToString(supervisorId)));
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
            throw new BadArgException("No member profile exists for the ID");
        }
        return memberProfileRepository.update(memberProfile);
    }
}
