package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

@Singleton
public class CheckInServicesImpl implements CheckInServices {

    private CheckInRepository checkinRepo;
    private MemberProfileRepository memberRepo;
    private SecurityService securityService;

    @Inject
    CurrentUserServices currentUserServices;

    public CheckInServicesImpl(CheckInRepository checkinRepo, MemberProfileRepository memberRepo, SecurityService securityService) {
        this.checkinRepo = checkinRepo;
        this.memberRepo = memberRepo;
        this.securityService = securityService;
//        this.currentUserServices = currentUserServices;
    }

    MemberProfile currentUser = currentUserServices.currentUserDetails(securityService.getAuthentication().get().getAttributes().get("email").toString());
    Boolean isAdmin = securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

    @Override
    public CheckIn save(@NotNull CheckIn checkIn) {

        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDate chkInDate = checkIn.getCheckInDate();

        if(checkIn.getId()!=null) {
            throw new CheckInBadArgException(String.format("Found unexpected id for checkin  %s", checkIn.getId()));
        } else if(memberId.equals(pdlId)) {
            throw new CheckInBadArgException(String.format("Team member id %s can't be same as PDL id", checkIn.getTeamMemberId()));
        } else if(!memberRepo.findById(memberId).isPresent()) {
            throw new CheckInBadArgException(String.format("Member %s doesn't exists", memberId));
        } else if(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId()) || !isAdmin) {
            throw new CheckInBadArgException(String.format("PDL %s is not associated with member %s", pdlId, memberId));
        } else if(chkInDate.isBefore(LocalDate.EPOCH) || chkInDate.isAfter(LocalDate.MAX)) {
            throw new CheckInBadArgException(String.format("Invalid date for checkin %s",memberId));
        } else if(!currentUser.getUuid().equals(checkIn.getTeamMemberId()) || !isAdmin) {
            throw new CheckInBadArgException(String.format("abcd"));
        }

        return checkinRepo.save(checkIn);
    }

    @Override
    public CheckIn read(@NotNull UUID id) {

        if(currentUser.getUuid().equals(id) || currentUser.getUuid().equals(memberRepo.findById(id).get().getPdlId()) || isAdmin) {
            return checkinRepo.findById(id).orElse(null);
        }

        throw new CheckInBadArgException(String.format("efgh"));
    }

    @Override
    public CheckIn update(@NotNull CheckIn checkIn) {

        final UUID id = checkIn.getId();
        final UUID memberId = checkIn.getTeamMemberId();
        final UUID pdlId = checkIn.getPdlId();
        LocalDate chkInDate = checkIn.getCheckInDate();

        if(id==null||!checkinRepo.findById(id).isPresent()) {
            throw new CheckInBadArgException(String.format("Unable to find checkin record with id %s", checkIn.getId()));
        } else if(memberId==null) {
            throw new CheckInBadArgException(String.format("Invalid checkin %s", checkIn));
        } else if(!memberRepo.findById(memberId).isPresent()) {
            throw new CheckInBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId()) || !isAdmin) {
            throw new CheckInBadArgException(String.format("PDL %s is not associated with member %s", pdlId, memberId));
        } else if(chkInDate.isBefore(LocalDate.EPOCH) || chkInDate.isAfter(LocalDate.MAX)) {
            throw new CheckInBadArgException(String.format("Invalid date for checkin %s",memberId));
        } else if(!currentUser.getUuid().equals(checkIn.getTeamMemberId()) || !isAdmin) {
            throw new CheckInBadArgException(String.format("abcd"));
        } else if(checkinRepo.findById(id).get().isCompleted() && !isAdmin) {
            throw new CheckInCompleteException(String.format("Checkin with id %s is complete and cannot be updated", checkIn.getId()));
        }

        return checkinRepo.update(checkIn);
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        Set<CheckIn> checkIn = new HashSet<>();

        if(isAdmin || teamMemberId.equals(currentUser.getUuid()) || pdlId.equals(currentUser.getUuid()) ||
                currentUser.getUuid().equals(memberRepo.findById(teamMemberId).get().getPdlId())) {

            checkinRepo.findAll().forEach(checkIn::add);
            if (teamMemberId != null) {
                checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
            } else if (pdlId != null) {
                checkIn.retainAll(checkinRepo.findByPdlId(pdlId));
            } else if (completed != null) {
                checkIn.retainAll(checkinRepo.findByCompleted(completed));
            }
            return checkIn;
        }

        throw new CheckInBadArgException(String.format("efgh"));
    }
}