package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.core.annotation.Nullable;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class MemberProfileServicesImpl implements MemberProfileServices {

    private final MemberProfileRepository memberProfileRepository;
    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;
    private final CheckInServices checkInServices;
    private final MemberSkillServices memberSkillServices;
    private final TeamMemberServices teamMemberServices;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;

    public MemberProfileServicesImpl(MemberProfileRepository memberProfileRepository,
                                     CurrentUserServices currentUserServices,
                                     RoleServices roleServices,
                                     CheckInServices checkInServices,
                                     MemberSkillServices memberSkillServices,
                                     TeamMemberServices teamMemberServices,
                                     MemberProfileRetrievalServices memberProfileRetrievalServices) {
        this.memberProfileRepository = memberProfileRepository;
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
        this.checkInServices = checkInServices;
        this.memberSkillServices = memberSkillServices;
        this.teamMemberServices = teamMemberServices;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
    }

    @Override
    public MemberProfile getById(@NotNull UUID id) {
        return memberProfileRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No member profile for id %s", id);
        });
    }

    @Override
    public Set<MemberProfile> findByValues(@Nullable String firstName,
                                           @Nullable String lastName,
                                           @Nullable String title,
                                           @Nullable UUID pdlId,
                                           @Nullable String workEmail,
                                           @Nullable UUID supervisorId,
                                           @Nullable Boolean terminated) {
        HashSet<MemberProfile> memberProfiles = new HashSet<>(memberProfileRepository.search(firstName, null, lastName, null, title,
                nullSafeUUIDToString(pdlId), workEmail, nullSafeUUIDToString(supervisorId), terminated));

        return memberProfiles;
    }

    @Override
    public MemberProfile saveProfile(MemberProfile memberProfile) {
        MemberProfile emailProfile = memberProfileRepository.findByWorkEmail(memberProfile.getWorkEmail()).orElse(null);

        validate(emailProfile == null || emailProfile.getId() == null || Objects.equals(memberProfile.getId(), emailProfile.getId())).orElseThrow(() -> {
            throw new AlreadyExistsException("Email %s already exists in database", memberProfile.getWorkEmail());
        });

        if (memberProfile.getId() == null) {
            return memberProfileRepository.save(memberProfile);
        }

        return memberProfileRepository.update(memberProfile);
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("Requires admin privileges");
        });

        MemberProfile memberProfile = memberProfileRepository.findById(id).orElse(null);
        Set<Role> userRoles = (memberProfile != null) ? roleServices.findUserRoles(memberProfile.getId()) : Collections.emptySet();

        validate(memberProfile != null).orElseThrow(() -> {
            throw new NotFoundException("No member profile for id");
        });
        validate(checkInServices.findByFields(id, null, null).isEmpty()).orElseThrow(() -> {
            throw new BadArgException("User %s cannot be deleted since Checkin record(s) exist", MemberProfileUtils.getFullName(memberProfile));
        });
        validate(memberSkillServices.findByFields(id, null).isEmpty()).orElseThrow(() -> {
            throw new BadArgException("User %s cannot be deleted since MemberSkill record(s) exist", MemberProfileUtils.getFullName(memberProfile));
        });
        validate(teamMemberServices.findByFields(null, id, null).isEmpty()).orElseThrow(() -> {
            throw new BadArgException("User %s cannot be deleted since TeamMember record(s) exist", MemberProfileUtils.getFullName(memberProfile));
        });
        validate(userRoles.isEmpty()).orElseThrow(() -> {
            throw new BadArgException("User %s cannot be deleted since user has PDL role", MemberProfileUtils.getFullName(memberProfile));
        });

        // Update PDL ID for all associated members before termination
        List<MemberProfile> pdlFor = memberProfileRepository.search(null, null, null,
                null, null, nullSafeUUIDToString(id), null, null, null);
        for (MemberProfile member : pdlFor) {
            member.setPdlId(null);
            memberProfileRepository.update(member);
        }
        memberProfileRepository.deleteById(id);
        return true;
    }

    @Override
    public MemberProfile findByName(@NotNull String firstName, @NotNull String lastName) {
        List<MemberProfile> searchResult = memberProfileRepository.search(firstName, null, lastName,
                null, null, null, null, null, null);

        validate(searchResult.size() == 1).orElseThrow(() -> {
            throw new BadArgException("Expected exactly 1 result. Found %s", searchResult.size());
        });

        return searchResult.get(0);
    }

    @Override
    public Optional<MemberProfile> findByWorkEmail(@NotNull String workEmail) {
        return memberProfileRetrievalServices.findByWorkEmail(workEmail);
    }

    @Override
    public List<MemberProfile> findAll() {
        return memberProfileRepository.findAll();
    }
}
