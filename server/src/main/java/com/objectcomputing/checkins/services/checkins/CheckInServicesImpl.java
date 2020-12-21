package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class CheckInServicesImpl implements CheckInServices {

    private final CheckInRepository checkinRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;

    public CheckInServicesImpl(CheckInRepository checkinRepo,
                               MemberProfileRepository memberRepo,
                               CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public CheckIn save(@NotNull CheckIn checkIn) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDateTime chkInDate = checkIn.getCheckInDate();

        validate(checkIn.getId()!=null, "Found unexpected id for checkin %s", checkIn.getId());
        validate(memberId.equals(pdlId), "Team member id %s can't be same as PDL id", checkIn.getTeamMemberId());
        validate(memberRepo.findById(memberId).isEmpty(), "Member %s doesn't exist", memberId);
        validate(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId()), "PDL %s is not associated with member %s", pdlId, memberId);
        validate((chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)), "Invalid date for checkin %s", memberId);
        if(!isAdmin) {
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

        if(!isAdmin) {
            // Limit read to Subject of check-in, PDL of subject and Admin
            validate((!currentUser.getId().equals(result.getTeamMemberId()) && !currentUser.getId().equals(result.getPdlId())), "You are not authorized to perform this operation");
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
        boolean isAdmin = currentUserServices.isAdmin();

        validate((id==null||!checkinRepo.findById(id).isPresent()), "Unable to find checkin record with id %s", checkIn.getId());
        validate(memberId==null, "Invalid checkin %s", checkIn.getId());
        validate(!memberRepo.findById(memberId).isPresent(), "Member %s doesn't exist", memberId);
        validate(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId()), "PDL %s is not associated with member %s", pdlId, memberId);
        validate((chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)), "Invalid date for checkin %s",memberId);
        if(!isAdmin) {
            // Limit update to subject of check-in, PDL of subject and Admin
            validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
            // Update is only allowed if the check in is not completed unless made by admin
            validate(checkinRepo.findById(id).get().isCompleted(), "Checkin with id %s is complete and cannot be updated", checkIn.getId());
        }

        return checkinRepo.update(checkIn);
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        Set<CheckIn> checkIn = new HashSet<>();
        checkinRepo.findAll().forEach(checkIn::add);

        if(teamMemberId != null) {
            Optional<MemberProfile> memberToSearch = memberRepo.findById(teamMemberId);
            if(memberToSearch.isPresent()) {
                // Limit findByTeamMemberId to Subject of check-in, PDL of subject and Admin
                validate((!isAdmin && currentUser != null &&
                        !currentUser.getId().equals(teamMemberId) &&
                        !currentUser.getId().equals(memberToSearch.get().getPdlId())),
                        "You are not authorized to perform this operation");
                checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
            } else checkIn.clear();
        } else if(pdlId != null) {
            // Limit findByPdlId to Subject of check-in, PDL of subject and Admin
            validate(!isAdmin && !currentUser.getId().equals(pdlId), "You are not authorized to perform this operation");
            checkIn.retainAll(checkinRepo.findByPdlId(pdlId));
        } else if(completed != null) {
            checkIn.retainAll(checkinRepo.findByCompleted(completed));
            if(!isAdmin) {
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

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if(isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}