package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.security.utils.SecurityService;

@Singleton
public class CheckInServicesImpl implements CheckInServices {

    private CheckInRepository checkinRepo;
    private MemberProfileRepository memberRepo;
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;

    public CheckInServicesImpl(CheckInRepository checkinRepo, MemberProfileRepository memberRepo, SecurityService securityService, CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.memberRepo = memberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public CheckIn save(@NotNull CheckIn checkIn) {

        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDateTime chkInDate = checkIn.getCheckInDate();

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if(checkIn.getId()!=null) {
            throw new CheckInBadArgException(String.format("Found unexpected id for checkin  %s", checkIn.getId()));
        } else if(memberId.equals(pdlId)) {
            throw new CheckInBadArgException(String.format("Team member id %s can't be same as PDL id", checkIn.getTeamMemberId()));
        } else if(memberRepo.findById(memberId).isEmpty()) {
            throw new CheckInBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId())) {
            throw new CheckInBadArgException(String.format("PDL %s is not associated with member %s", pdlId, memberId));
        } else if(chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)) {
            throw new CheckInBadArgException(String.format("Invalid date for checkin %s",memberId));
        } else if(!isAdmin) {
            if(!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())) {
                // Limit create to subject of check-in, PDL of subject and Admin
                throw new CheckInBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
            }
        }

        return checkinRepo.save(checkIn);
    }

    @Override
    public CheckIn read(@NotNull UUID id) {

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        CheckIn result = checkinRepo.findById(id).orElse(null);

        if (result == null) {
            throw new CheckInBadArgException(String.format("Invalid checkin id %s", id));
        } else if(!isAdmin) {
            if(!currentUser.getId().equals(result.getTeamMemberId()) && !currentUser.getId().equals(result.getPdlId())) {
                // Limit read to Subject of check-in, PDL of subject and Admin
                throw new CheckInBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
            }
        }

        return result;
    }

    @Override
    public CheckIn update(@NotNull CheckIn checkIn) {

        final UUID id = checkIn.getId();
        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDateTime chkInDate = checkIn.getCheckInDate();

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if(id==null||!checkinRepo.findById(id).isPresent()) {
            throw new CheckInBadArgException(String.format("Unable to find checkin record with id %s", checkIn.getId()));
        } else if(memberId==null) {
            throw new CheckInBadArgException(String.format("Invalid checkin %s", checkIn));
        } else if(!memberRepo.findById(memberId).isPresent()) {
            throw new CheckInBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId())) {
            throw new CheckInBadArgException(String.format("PDL %s is not associated with member %s", pdlId, memberId));
        } else if(chkInDate.isBefore(Util.MIN) || chkInDate.isAfter(Util.MAX)) {
            throw new CheckInBadArgException(String.format("Invalid date for checkin %s",memberId));
        } else if(!isAdmin) {
            if(!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())) {
                // Limit update to subject of check-in, PDL of subject and Admin
                throw new CheckInBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
            } else if(checkinRepo.findById(id).get().isCompleted()) {
                // Update is only allowed if the check in is not completed unless made by admin
                throw new CheckInBadArgException(String.format("Checkin with id %s is complete and cannot be updated", checkIn.getId()));
            }
        }

        return checkinRepo.update(checkIn);
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        Set<CheckIn> checkIn = new HashSet<>();
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        checkinRepo.findAll().forEach(checkIn::add);

        if(isAdmin) {
            if (teamMemberId != null) {
                checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
            } else if (pdlId != null) {
                checkIn.retainAll(checkinRepo.findByPdlId(pdlId));
            } else if (completed != null) {
                checkIn.retainAll(checkinRepo.findByCompleted(completed));
            }
        } else {
            if(teamMemberId != null) {
                if(!currentUser.getId().equals(teamMemberId) && !currentUser.getId().equals(memberRepo.findById(teamMemberId).get().getPdlId())) {
                    // Limit findByTeamMemberId to Subject of check-in, PDL of subject and Admin
                    throw new CheckInBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
                } else {
                    checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
                }
            } else if(pdlId != null) {
                if (!currentUser.getId().equals(pdlId)) {
                    // Limit findByPdlId to Subject of check-in, PDL of subject and Admin
                    throw new CheckInBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
                } else {
                    checkIn.retainAll(checkinRepo.findByPdlId(pdlId));
                }
            } else if(completed != null) {
                // Limit findByCompleted to retrieve only the records pertinent to current user (if not admin)
                checkIn = checkIn.stream()
                            .filter(c -> c.getTeamMemberId().equals(currentUser.getId()) || c.getPdlId().equals(currentUser.getId()))
                            .filter(c -> c.isCompleted() == completed)
                            .collect(Collectors.toSet());
            } else {
                // Limit findAll to only Admin
                throw new CheckInBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
            }
        }

        return checkIn;
    }
}