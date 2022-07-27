package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
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

import static com.objectcomputing.checkins.services.validate.Validation.validate;

@Singleton
public class CheckInServicesImpl implements CheckInServices {

    public static final Logger LOG = LoggerFactory.getLogger(CheckInServicesImpl.class);

    private final CheckInRepository checkinRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;

    public CheckInServicesImpl(CheckInRepository checkinRepo,
                               MemberProfileRepository memberRepo,
                               CurrentUserServices currentUserServices, RoleServices roleServices) {
        this.checkinRepo = checkinRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
    }

    @Override
    public Boolean accessGranted(@NotNull UUID checkinId, @NotNull UUID memberId) {
        boolean grantAccess = false;

        MemberProfile memberTryingToGainAccess = memberRepo.findById(memberId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Member %s not found", memberId));
        });
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Checkin %s not found", checkinId));
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
            MemberProfile teamMemberOnCheckin = memberRepo.findById(checkinRecord.getTeamMemberId()).orElseThrow(() -> {
                throw new NotFoundException("Team member not found %s not found", checkinRecord.getTeamMemberId());
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
        Optional<MemberProfile> memberProfileOfTeamMember = memberRepo.findById(memberId);

        validate(checkIn.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Found unexpected id for checkin %s", checkIn.getId());
        });
        validate(!memberId.equals(pdlId)).orElseThrow(() -> {
            throw new BadArgException("Team member id %s can't be same as PDL id", checkIn.getTeamMemberId());
        });
        validate(memberProfileOfTeamMember.isPresent()).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", memberId);
        });
        validate(pdlId.equals(memberProfileOfTeamMember.get().getPdlId())).orElseThrow(() -> {
            throw new BadArgException("PDL %s is not associated with member %s", pdlId, memberId);
        });
        validate(chkInDate.isAfter(Util.MIN) && chkInDate.isBefore(Util.MAX)).orElseThrow(() -> {
            throw new BadArgException("Invalid date for checkin %s", checkIn.getTeamMemberId());
        });

        boolean isTeamMember = currentUser.getId().equals(checkIn.getTeamMemberId());
        boolean isPdl = currentUser.getId().equals(checkIn.getPdlId());

        validate(isAdmin || isTeamMember || isPdl).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        return checkinRepo.save(checkIn);
    }

    @Override
    public CheckIn read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        CheckIn result = checkinRepo.findById(id).orElseThrow(() -> {
            throw new BadArgException("Invalid checkin id %s", id);
        });

        validate(isAdmin || accessGranted(id, currentUser.getId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        return result;
    }

    @Override
    public CheckIn update(@NotNull CheckIn checkIn) {

        final UUID id = checkIn.getId();
        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDateTime chkInDate = checkIn.getCheckInDate();

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        Optional<MemberProfile> memberProfileOfTeamMember = memberRepo.findById(memberId);
        boolean isAdmin = currentUserServices.isAdmin();

        validate(id != null).orElseThrow(() -> {
            throw new BadArgException("Unable to find checkin record with id %s", id);
        });

        CheckIn associatedCheckin = checkinRepo.findById(id).orElseThrow(() -> {
            throw new BadArgException("Checkin %s doesn't exist", id);
        });
        validate(memberProfileOfTeamMember.isPresent()).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", memberId);
        });
        validate(pdlId.equals(memberProfileOfTeamMember.get().getPdlId())).orElseThrow(() -> {
            throw new BadArgException("PDL %s is not associated with member %s", pdlId, memberId);
        });
        validate(chkInDate.isAfter(Util.MIN) && chkInDate.isBefore(Util.MAX)).orElseThrow(() -> {
            throw new BadArgException("Invalid date for checkin %s", memberId);
        });

        validate(isAdmin || accessGranted(id, currentUser.getId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });
        validate(isAdmin || !associatedCheckin.isCompleted()).orElseThrow(() -> {
            throw new PermissionException("Checkin with id %s is complete and cannot be updated", checkIn.getId());
        });

        return checkinRepo.update(checkIn);
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        Set<CheckIn> checkIn = new HashSet<>(checkinRepo.findAll());

        if (teamMemberId != null) {
            Optional<MemberProfile> memberToSearch = memberRepo.findById(teamMemberId);
            if (memberToSearch.isPresent()) {
                // Limit findByTeamMemberId to Subject of check-in, PDL of subject and Admin
                boolean isTeamMember = currentUser.getId().equals(teamMemberId);
                boolean isPdl = currentUser.getId().equals(memberToSearch.get().getPdlId());
                validate(isAdmin || isTeamMember || isPdl).orElseThrow(() -> {
                    throw new PermissionException("You are not authorized to perform this operation");
                });
                checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
            } else {
                checkIn.clear();
            }
        } else if (pdlId != null) {
            // Limit findByPdlId to Subject of check-in, PDL of subject and Admin
            validate(isAdmin || currentUser.getId().equals(pdlId)).orElseThrow(() -> {
                throw new PermissionException("You are not authorized to perform this operation");
            });
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
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("You are not authorized to perform this operation");
            });
        }

        return checkIn;
    }
}