package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class CheckInServicesImpl implements CheckInServices {

    public static final Logger LOG = LoggerFactory.getLogger(CheckInServicesImpl.class);

    private final CheckInRepository checkinRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;
    private final PermissionServices permissionServices;

    public CheckInServicesImpl(CheckInRepository checkinRepo,
                               MemberProfileRepository memberRepo,
                               CurrentUserServices currentUserServices,
                               RoleServices roleServices, PermissionServices permissionServices) {
        this.checkinRepo = checkinRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
        this.permissionServices = permissionServices;
    }

    @Override
    public Boolean hasPermission(@NotNull UUID memberId, @NotNull Permissions permission) {
        boolean hasPermission = false;
        List<Permission> userPermissions = permissionServices.findUserPermissions(memberId);
        if (userPermissions.size() > 0) {
            hasPermission = userPermissions.stream().map(Permission::getPermission).anyMatch(str -> str.equals(permission.name()));
            LOG.debug("Member has elevated access permisson: {}", hasPermission);
        }
        return hasPermission;
    }

    @Override
    public Boolean accessGranted(@NotNull UUID checkinId, @NotNull UUID memberId) {
        memberRepo.findById(memberId).orElseThrow(() -> new NotFoundException(String.format("Member %s not found", memberId)));

        boolean canViewAllCheckins = canViewAllCheckins(memberId);
        
        // TODO: the check for Admin as a role should be removed when permissions are fully implemented
        boolean isAdmin = false;
        if (roleServices.findByRole(RoleType.ADMIN.name()).isPresent()){
            isAdmin = roleServices.findUserRoles(memberId)
                .contains(roleServices.findByRole(RoleType.ADMIN.name()).get());
            LOG.debug("Member is Admin: {}", isAdmin);
        }

        Boolean grantAccess = false;
        if(canViewAllCheckins || isAdmin){
            grantAccess = true;
        } else {
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElseThrow(() -> new NotFoundException(String.format("Checkin %s not found", checkinId)));
            MemberProfile teamMemberOnCheckin = memberRepo.findById(checkinRecord.getTeamMemberId()).orElseThrow(() ->
                new NotFoundException(String.format("Team member not found %s not found", checkinRecord.getTeamMemberId())));
            UUID currentPdlId = teamMemberOnCheckin.getPdlId();

            LOG.debug("Member: {}", memberId);
            LOG.debug("Checkin Member: {}", checkinRecord.getTeamMemberId());
            LOG.debug("PDL on Checkin: {}", checkinRecord.getPdlId());
            LOG.debug("Current PDL: {}", currentPdlId);

            if (memberId.equals(checkinRecord.getTeamMemberId())
                    || memberId.equals(checkinRecord.getPdlId())
                    || memberId.equals(currentPdlId)) {
                grantAccess = true;
            }
        }
        return grantAccess;
    }

    @Override
    public CheckIn save(@NotNull CheckIn checkIn) {
        validate(checkIn.getId() != null, "Found unexpected id for checkin %s", checkIn.getId());

        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        validate(memberId.equals(pdlId), "Team member id %s can't be same as PDL id", checkIn.getTeamMemberId());

        Optional<MemberProfile> memberProfileOfTeamMember = memberRepo.findById(memberId);
        validate(memberProfileOfTeamMember.isEmpty(), "Member %s doesn't exist", memberId);
        validate(!pdlId.equals(memberProfileOfTeamMember.get().getPdlId()), "PDL %s is not associated with member %s", pdlId, memberId);

        LocalDateTime chkInDate = checkIn.getCheckInDate();
        validate((chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)), "Invalid date for checkin %s", memberId);

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canUpdateAllCheckins = canUpdateAllCheckins(currentUser.getId());

        if (!canUpdateAllCheckins) {
            validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
        }

        return checkinRepo.save(checkIn);
    }

    @Override
    public CheckIn read(@NotNull UUID checkinId) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        validate(!accessGranted(checkinId, currentUserId), "You are not authorized to perform this operation");

        return checkinRepo.findById(checkinId).orElse(null);
    }

    @Override
    public CheckIn update(@NotNull CheckIn checkIn) {
        final UUID id = checkIn.getId();
        validate(id == null, "Unable to find checkin record with id %s", checkIn.getId());
        
        final UUID memberId = checkIn.getTeamMemberId();
        validate(memberId == null, "Invalid checkin %s", checkIn.getId());

        Optional<MemberProfile> memberProfileOfTeamMember = memberRepo.findById(memberId);
        validate(memberProfileOfTeamMember.isEmpty(), "Member %s doesn't exist", memberId);

        final UUID pdlId = checkIn.getPdlId();
        validate(!pdlId.equals(memberProfileOfTeamMember.get().getPdlId()), "PDL %s is not associated with member %s", pdlId, memberId);

        LocalDateTime chkInDate = checkIn.getCheckInDate();
        validate((chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)), "Invalid date for checkin %s", memberId);

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canUpdateAllCheckins = canUpdateAllCheckins(currentUser.getId());

        if (!canUpdateAllCheckins) {
            Optional<CheckIn> associatedCheckin = checkinRepo.findById(id);
            validate(associatedCheckin.isEmpty(), "Checkin %s doesn't exist", id);
            // Limit update to subject of check-in, PDL of subject and user with canViewAllCheckins permission
            validate(!accessGranted(id, currentUser.getId()), "You are not authorized to perform this operation");
            // Update is only allowed if the check in is not completed unless made by user with canUpdateAllCheckins permission
            validate(associatedCheckin.get().isCompleted(), "Checkin with id %s is complete and cannot be updated", checkIn.getId());
        }

        return checkinRepo.update(checkIn);
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        final UUID currentUserId = currentUser.getId();
        boolean canViewAllCheckins = canViewAllCheckins(currentUserId);

        Set<CheckIn> checkIns = new HashSet<>();

        if (teamMemberId != null) { // find by teamMemberId
            Optional<MemberProfile> teamMemberProfile = memberRepo.findById(teamMemberId);
            if (teamMemberProfile.isPresent()) {
                boolean currentUserExists = currentUser != null;
                boolean currentUserIsCheckinParticipant = currentUserId.equals(teamMemberId) || currentUserId.equals(teamMemberProfile.get().getPdlId());

                validate((currentUserExists && !canViewAllCheckins && !currentUserIsCheckinParticipant), "You are not authorized to perform this operation");

                checkinRepo.findByTeamMemberId(teamMemberId).forEach(checkIns::add);
            }
        } else if (pdlId != null) { // find by pdlId
            boolean currentUserIsPdl = currentUserId.equals(pdlId);

            validate(!canViewAllCheckins && !currentUserIsPdl, "You are not authorized to perform this operation");

            checkinRepo.findByPdlId(pdlId).forEach(checkIns::add);
        } else if (completed != null) { // find completed
            checkinRepo.findByCompleted(completed).forEach(checkIns::add);;
            if (!canViewAllCheckins) {
                // Limit findByCompleted to retrieve only the records pertinent to current user (if not user with canViewAllCheckins permission)
                checkIns = checkIns.stream()
                        .filter(checkIn -> checkIn.getTeamMemberId().equals(currentUserId) || checkIn.getPdlId().equals(currentUserId))
                        .collect(Collectors.toSet());
            }
        } else { // find all
            validate(!canViewAllCheckins, "You are not authorized to perform this operation");
            checkinRepo.findAll().forEach(checkIns::add);
        }

        return checkIns;
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

    @Override
    public Boolean canViewAllCheckins(UUID memberId) {
        return hasPermission(memberId, Permissions.CAN_VIEW_ALL_CHECKINS);
    }

    @Override
    public Boolean canUpdateAllCheckins(UUID memberId) {
        return hasPermission(memberId, Permissions.CAN_UPDATE_ALL_CHECKINS);
    }
}