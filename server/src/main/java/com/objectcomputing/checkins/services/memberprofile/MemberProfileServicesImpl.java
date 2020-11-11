package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.security.InsufficientPrivelegesException;
import com.objectcomputing.checkins.services.member_skill.MemberSkillAlreadyExistsException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class MemberProfileServicesImpl implements MemberProfileServices {

    private final MemberProfileRepository memberProfileRepository;
    private final CurrentUserServices currentUserServices;
    private final TeamMemberRepository teamMemberRepository;

    public MemberProfileServicesImpl(MemberProfileRepository memberProfileRepository,
                                     CurrentUserServices currentUserServices,
                                     TeamMemberRepository teamMemberRepository) {
        this.memberProfileRepository = memberProfileRepository;
        this.currentUserServices = currentUserServices;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public MemberProfile getById(UUID id) {
        Optional<MemberProfile> memberProfile = memberProfileRepository.findById(id);
        if (memberProfile.isEmpty()) {
            throw new MemberProfileDoesNotExistException("No member profile for id");
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

    @Override
    public Boolean deleteProfile(UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new InsufficientPrivelegesException("Requires admin privileges");
        }
        List<MemberProfile> pdlFor = memberProfileRepository.search(null, null, nullSafeUUIDToString(id), null);
        for (MemberProfile member : pdlFor) {
            member.setPdlId(null);
            memberProfileRepository.update(member);
        }
        memberProfileRepository.deleteById(id);
        return true;
    }
}
