package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.member_skill.MemberSkillAlreadyExistsException;
import com.objectcomputing.checkins.services.member_skill.MemberSkillNotFoundException;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class MemberProfileServicesImpl implements MemberProfileServices {

    private final MemberProfileRepository memberProfileRepository;

    public MemberProfileServicesImpl(MemberProfileRepository memberProfileRepository) {
        this.memberProfileRepository = memberProfileRepository;
    }

    @Override
    public MemberProfileEntity getById(UUID id) {
        Optional<MemberProfileEntity> memberProfile = memberProfileRepository.findById(id);
        if (memberProfile.isEmpty()) {
            throw new MemberProfileDoesNotExistException("No member profile for id");
        }
        return memberProfile.get();
    }

    @Override
    public Set<MemberProfileEntity> findByValues(@Nullable String name, @Nullable String title, @Nullable UUID pdlId, @Nullable String workEmail) {
        Set<MemberProfileEntity> memberProfileEntities = new HashSet<>(
                memberProfileRepository.search(name, title, nullSafeUUIDToString(pdlId), workEmail));

        return memberProfileEntities;
    }

    @Override
    public MemberProfileEntity saveProfile(MemberProfileEntity memberProfileEntity) {
        MemberProfileEntity emailProfile = memberProfileRepository.findByWorkEmail(memberProfileEntity.getWorkEmail()).orElse(null);
        if(emailProfile != null && emailProfile.getId() != null && !Objects.equals(memberProfileEntity.getId(), emailProfile.getId())) {
            throw new MemberSkillAlreadyExistsException(String.format("Email %s already exists in database",
                    memberProfileEntity.getWorkEmail()));
        }
        if (memberProfileEntity.getId() == null) {
            return memberProfileRepository.save(memberProfileEntity);
        }
        if (memberProfileRepository.findById(memberProfileEntity.getId()) == null) {
            throw new MemberProfileBadArgException("No member profile exists for the ID");
        }
        return memberProfileRepository.update(memberProfileEntity);
    }

    @Override
    public MemberProfileEntity findByName(@NotNull String name) {
        List<MemberProfileEntity> searchResult = memberProfileRepository.search(name, null, null, null);
        if (searchResult.size() != 1) {
            throw new MemberProfileDoesNotExistException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }
}
