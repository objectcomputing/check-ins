package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
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
import java.util.stream.Collectors;

@Singleton
public class CheckInServicesImpl implements CheckInServices {

    public static final Logger LOG = LoggerFactory.getLogger(CheckInServicesImpl.class);

    private final CheckInRepository checkinRepo;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;

    public CheckInServicesImpl(CheckInRepository checkinRepo,
                               MemberProfileRetrievalServices memberProfileRetrievalServices,
                               CurrentUserServices currentUserServices,
                               RoleServices roleServices) {
        this.checkinRepo = checkinRepo;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
    }

    @Override
    public Boolean accessGranted(@NotNull UUID checkinId, @NotNull UUID memberId) {
        Boolean grantAccess = false;

        MemberProfile memberTryingToGainAccess = memberProfileRetrievalServices.getById(memberId).orElseThrow(() -> {
            throw new BadArgException("Member %s does not exist", memberId);
        });
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElseThrow(() -> {
            throw new NotFoundException("Checkin %s not found", checkinId);
        });

        boolean isAdmin = false;
        if (roleServices.findByRole(RoleType.ADMIN.name()).isPresent()){
            isAdmin = roleServices.findUserRoles(memberTryingToGainAccess.getId())
                    .contains(roleServices.findByRole(RoleType.ADMIN.name()).get());
            LOG.debug("Member is Admin: {}", isAdmin);
        }

        if(isAdmin){
            grantAccess = true;
        } else {
            MemberProfile teamMemberOnCheckin = memberProfileRetrievalServices.getById(checkinRecord.getTeamMemberId()).orElseThrow(() -> {
                throw new NotFoundException("Member %s does not exist", checkinRecord.getTeamMemberId());
            });
            UUID currentPdlId = teamMemberOnCheckin.getPdlId();

            LOG.debug("Member: {}", memberTryingToGainAccess.getId());
            LOG.debug("Checkin Member: {}", checkinRecord.getTeamMemberId());
            LOG.debug("PDL on Checkin: {}", checkinRecord.getPdlId());
            LOG.debug("Current PDL: {}", currentPdlId);

            if (memberTryingToGainAccess.getId().equals(checkinRecord.getTeamMemberId())
                    || memberTryingToGainAccess.getId().equals(checkinRecord.getPdlId())
                    || memberTryingToGainAccess.getId().equals(currentPdlId)) {
                grantAccess = true;
            }
        }
        return grantAccess;
    }

    @Override
    public CheckIn save(@NotNull CheckIn checkIn) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDateTime chkInDate = checkIn.getCheckInDate();
        MemberProfile memberProfileOfTeamMember = memberProfileRetrievalServices.getById(memberId).orElseThrow(() -> {
            throw new BadArgException("Member %s does not exist", memberId);
        });

        validate(checkIn.getId() != null, "Found unexpected id for checkin %s", checkIn.getId());
        validate(memberId.equals(pdlId), "Team member id %s can't be same as PDL id", checkIn.getTeamMemberId());
        validate(!pdlId.equals(memberProfileOfTeamMember.getPdlId()), "PDL %s is not associated with member %s", pdlId, memberId);
        validate((chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)), "Invalid date for checkin %s", memberId);
        if (!isAdmin) {
            validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
        }

        return checkinRepo.save(checkIn);
    }

    @Override
    public CheckIn read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        CheckIn result = checkinRepo.findById(id).orElse(null);
        validate((result == null), "Invalid checkin id %s", id);

        if (!isAdmin) {
            // Limit read to Subject of check-in, PDL of subject and Admin
            validate(!accessGranted(id, currentUser.getId()), "You are not authorized to perform this operation");
        }

        return result;
    }

    @Override
    public CheckIn update(@NotNull CheckIn checkIn) {

        final UUID id = checkIn.getId();
        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDateTime chkInDate = checkIn.getCheckInDate();

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        MemberProfile memberProfileOfTeamMember = memberProfileRetrievalServices.getById(memberId).orElseThrow(() -> {
            throw new BadArgException("Member %s does not exist", memberId);
        });
        boolean isAdmin = currentUserServices.isAdmin();

        validate(id == null, "Unable to find checkin record with id %s", checkIn.getId());
        Optional<CheckIn> associatedCheckin = checkinRepo.findById(id);
        validate(memberId == null, "Invalid checkin %s", checkIn.getId());
        validate(memberProfileOfTeamMember == null, "Member %s doesn't exist", memberId);
        validate(associatedCheckin.isEmpty(), "Checkin %s doesn't exist", id);
        validate(!pdlId.equals(memberProfileOfTeamMember.getPdlId()), "PDL %s is not associated with member %s", pdlId, memberId);
        validate((chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)), "Invalid date for checkin %s", memberId);
        if (!isAdmin) {
            // Limit update to subject of check-in, PDL of subject and Admin
            validate(!accessGranted(id, currentUser.getId()), "You are not authorized to perform this operation");
            // Update is only allowed if the check in is not completed unless made by admin
            validate(associatedCheckin.get().isCompleted(), "Checkin with id %s is complete and cannot be updated", checkIn.getId());
        }

        return checkinRepo.update(checkIn);
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        Set<CheckIn> checkIn = new HashSet<>();
        checkinRepo.findAll().forEach(checkIn::add);

        if (teamMemberId != null) {
            MemberProfile memberToSearch = memberProfileRetrievalServices.getById(teamMemberId).orElseThrow(() -> {
                throw new BadArgException("Member %s does not exist", teamMemberId);
            });
            if (memberToSearch != null) {
                // Limit findByTeamMemberId to Subject of check-in, PDL of subject and Admin
                validate((!isAdmin && currentUser != null &&
                                !currentUser.getId().equals(teamMemberId) &&
                                !currentUser.getId().equals(memberToSearch.getPdlId())),
                        "You are not authorized to perform this operation");
                checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
            } else checkIn.clear();
        } else if (pdlId != null) {
            // Limit findByPdlId to Subject of check-in, PDL of subject and Admin
            validate(!isAdmin && !currentUser.getId().equals(pdlId), "You are not authorized to perform this operation");
            checkIn.retainAll(checkinRepo.findByPdlId(pdlId));
        } else if (completed != null) {
            checkIn.retainAll(checkinRepo.findByCompleted(completed));
            if (!isAdmin) {
                // Limit findByCompleted to retrieve only the records pertinent to current user (if not admin)
                checkIn = checkIn.stream()
                        .filter(c -> c.getTeamMemberId().equals(currentUser.getId()) || c.getPdlId().equals(currentUser.getId()))
                        .collect(Collectors.toSet());
            }
        } else {
            // Limit findAll to only Admin
            validate(!isAdmin, "You are not authorized to perform this operation");
        }

        return checkIn;
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(message, args);
        }
    }
}