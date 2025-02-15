package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.time.LocalDate;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
@CacheConfig("member-cache")
public class MemberProfileServicesImpl implements MemberProfileServices {
    private static final Logger LOG = LoggerFactory.getLogger(MemberProfileServicesImpl.class);

    private final MemberProfileRepository memberProfileRepository;
    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;
    private final CheckInServices checkInServices;
    private final MemberSkillServices memberSkillServices;
    private final TeamMemberServices teamMemberServices;
    private final EmailSender emailSender;

    public MemberProfileServicesImpl(MemberProfileRepository memberProfileRepository,
                                     CurrentUserServices currentUserServices,
                                     RoleServices roleServices,
                                     CheckInServices checkInServices,
                                     MemberSkillServices memberSkillServices,
                                     TeamMemberServices teamMemberServices,
                                     @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender) {
        this.memberProfileRepository = memberProfileRepository;
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
        this.checkInServices = checkInServices;
        this.memberSkillServices = memberSkillServices;
        this.teamMemberServices = teamMemberServices;
        this.emailSender = emailSender;
    }

    @Override
    @Cacheable
    public MemberProfile getById(@NotNull UUID id) {
        Optional<MemberProfile> optional = memberProfileRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("No member profile for id " + id);
        }
        MemberProfile memberProfile = optional.get();
        if (!hasAdministerPermission()) {
            memberProfile.clearBirthYear();
        }
        return memberProfile;
    }

    @Cacheable
    @Override
    public MemberProfile findByWorkEmail(@NotNull String workEmail) {
        return memberProfileRepository.findByWorkEmail(workEmail).orElseThrow(() ->
                new NotFoundException("Member not found")
        );
    }

    @Override
    public Set<MemberProfile> findByValues(@Nullable String firstName,
                                           @Nullable String lastName,
                                           @Nullable String title,
                                           @Nullable UUID pdlId,
                                           @Nullable String workEmail,
                                           @Nullable UUID supervisorId,
                                           @Nullable Boolean terminated) {
        Set<MemberProfile> memberProfiles = new HashSet<>(memberProfileRepository.search(firstName, null, lastName, null, title,
                nullSafeUUIDToString(pdlId), workEmail, nullSafeUUIDToString(supervisorId), terminated));
        if (!hasAdministerPermission()) {
            for (MemberProfile memberProfile : memberProfiles) {
                memberProfile.clearBirthYear();
            }
        }
        return memberProfiles;
    }

    @Override
    @CacheInvalidate(cacheNames = {"member-cache"})
    @RequiredPermission(Permission.CAN_CREATE_ORGANIZATION_MEMBERS)
    public MemberProfile saveProfile(MemberProfile memberProfile) {
        MemberProfile emailProfile = memberProfileRepository.findByWorkEmail(memberProfile.getWorkEmail()).orElse(null);

        if (emailProfile != null && emailProfile.getId() != null && !Objects.equals(memberProfile.getId(), emailProfile.getId())) {
            throw new AlreadyExistsException(String.format("Email %s already exists in database",
                    memberProfile.getWorkEmail()));
        }

        if (memberProfile.getId() == null) {
            MemberProfile createdMemberProfile = memberProfileRepository.save(memberProfile);
            emailAssignment(createdMemberProfile, true); // PDL
            emailAssignment(createdMemberProfile, false); // Supervisor
            return createdMemberProfile;
        } else {
            throw new BadArgException("New member created with an id");
        }
    }

    public void emailAssignment(MemberProfile member, boolean isPDL) {
        UUID roleId = isPDL ? member.getPdlId() : member.getSupervisorid();
        String role = isPDL ? "PDL" : "supervisor";
        if (roleId != null) {
            if (member.getLastName() != null && member.getFirstName() != null && member.getWorkEmail() != null) {
                Optional<MemberProfile> roleProfileOptional = memberProfileRepository.findById(roleId);

                if (roleProfileOptional.isPresent()) {
                    MemberProfile roleProfile = roleProfileOptional.get();

                    if (roleProfile.getWorkEmail() != null) {
                        String subject = "You have been assigned as the " + role + " of " + member.getFirstName() + " " + member.getLastName();
                        String body = member.getFirstName() + " " + member.getLastName() + " will now report to you as their " + role + ". Please engage with them: " + member.getWorkEmail();

                        emailSender.sendEmail(null, null, subject, body, roleProfile.getWorkEmail());
                    } else {
                        LOG.warn("Unable to send email regarding {} {}'s {} update as the {} was unable to be pulled up correctly",
                                member.getFirstName(), member.getLastName(), role, role);
                    }
                } else {
                    LOG.warn("Unable to send email regarding {} {}'s {} update as the {} was not found",
                            member.getFirstName(), member.getLastName(), role, role);
                }
            } else {
                LOG.warn("Unable to send email regarding as member was not valid and missing required fields: {}", member);
            }
        }
    }

    @Override
    @CacheInvalidate(cacheNames = {"member-cache"})
    @RequiredPermission(Permission.CAN_DELETE_ORGANIZATION_MEMBERS)
    public boolean deleteProfile(@NotNull UUID id) {
        MemberProfile memberProfile = memberProfileRepository.findById(id).orElse(null);
        Set<Role> userRoles = (memberProfile != null) ? roleServices.findUserRoles(memberProfile.getId()) : Collections.emptySet();

        if (memberProfile == null) {
            throw new NotFoundException("No member profile for id");
        } else if (!checkInServices.findByFields(id, null, null).isEmpty()) {
            throw new BadArgException(String.format("User %s cannot be deleted since Checkin record(s) exist", MemberProfileUtils.getFullName(memberProfile)));
        } else if (!memberSkillServices.findByFields(id, null).isEmpty()) {
            throw new BadArgException(String.format("User %s cannot be deleted since MemberSkill record(s) exist", MemberProfileUtils.getFullName(memberProfile)));
        } else if (!teamMemberServices.findByFields(null, id, null).isEmpty()) {
            throw new BadArgException(String.format("User %s cannot be deleted since TeamMember record(s) exist", MemberProfileUtils.getFullName(memberProfile)));
        } else if (!userRoles.isEmpty()) {
            throw new BadArgException(String.format("User %s cannot be deleted since user has one or more roles", MemberProfileUtils.getFullName(memberProfile)));
        }

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
    @Cacheable(parameters = {"firstName", "lastName"})
    public MemberProfile findByName(@NotNull String firstName, @NotNull String lastName) {
        List<MemberProfile> searchResult = memberProfileRepository.search(firstName, null, lastName,
                null, null, null, null, null, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public List<MemberProfile> findAll() {
        return memberProfileRepository.findAll();
    }

    @Override
    @Cacheable
    public List<MemberProfile> getSupervisorsForId(UUID id) {
        List<MemberProfile> supervisorsForId = memberProfileRepository.findSupervisorsForId(id);
        if (!hasAdministerPermission()) {
            for (MemberProfile memberProfile : supervisorsForId) {
                memberProfile.clearBirthYear();
            }
        }
        return supervisorsForId;
    }

    @Override
    @Cacheable
    public List<MemberProfile> getSubordinatesForId(UUID id) {
        List<MemberProfile> subordinatesForId = memberProfileRepository.findSubordinatesForId(id);
        if (!hasAdministerPermission()) {
            for (MemberProfile memberProfile : subordinatesForId) {
                memberProfile.clearBirthYear();
            }
        }
        return subordinatesForId;
    }

    @Override
    @CacheInvalidate(cacheNames = {"member-cache"})
    public MemberProfile updateProfile(MemberProfile memberProfile) {
        if (memberProfile.getId() == null) {
            throw new BadArgException("Null profile id in update");
        }

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        if (!currentUserServices.hasPermission(Permission.CAN_EDIT_ALL_ORGANIZATION_MEMBERS) &&
            (currentUser == null || !currentUser.getId().equals(memberProfile.getId()))) {
             throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        MemberProfile emailProfile = memberProfileRepository.findByWorkEmail(memberProfile.getWorkEmail()).orElse(null);

        if (emailProfile != null && emailProfile.getId() != null && !Objects.equals(memberProfile.getId(), emailProfile.getId())) {
            throw new AlreadyExistsException(String.format("Email %s already exists in database",
                    memberProfile.getWorkEmail()));
        }

        Optional<MemberProfile> existingProfileOpt = memberProfileRepository.findById(memberProfile.getId());
        MemberProfile updatedMemberProfile = memberProfileRepository.update(memberProfile);
        if (existingProfileOpt.isEmpty()) {
            LOG.error("MemberProfile with id {} not found", memberProfile.getId());
        } else {
            MemberProfile existingProfile = existingProfileOpt.get();

            boolean pdlChanged = !Objects.equals(existingProfile.getPdlId(), memberProfile.getPdlId());
            boolean supervisorChanged = !Objects.equals(existingProfile.getSupervisorid(), memberProfile.getSupervisorid());

            if (pdlChanged) {
                emailAssignment(updatedMemberProfile, true); // PDL
            }
            if (supervisorChanged) {
                emailAssignment(updatedMemberProfile, false); // Supervisor
            }
        }

        return updatedMemberProfile;
    }

    @Override
    @CacheInvalidate(cacheNames = {"member-cache"})
    public void updateLastSeen(UUID id) {
        Optional<MemberProfile> profile = memberProfileRepository.findById(id);
        if (profile.isPresent()) {
          MemberProfile memberProfile = profile.get();
          memberProfile.setLastSeen(LocalDate.now());
          memberProfileRepository.update(memberProfile);
        }
    }

    private boolean hasAdministerPermission() {
        return currentUserServices.hasPermission(Permission.CAN_EDIT_ALL_ORGANIZATION_MEMBERS);
    }
}
