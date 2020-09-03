package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;


public class CheckInServicesImpl implements CheckInServices {

    @Inject
    private CheckInRepository checkinRepo;

    @Inject
    private MemberProfileRepository memberRepo;

    @Inject
    SecurityService securityService;

    @Override
    public CheckIn save(CheckIn checkIn) {
        CheckIn checkInRet = null;

        if(checkIn!=null) {

            final UUID memberId = checkIn.getTeamMemberId();
            final UUID pdlId = checkIn.getPdlId();
            LocalDate chkInDate = checkIn.getCheckInDate();

            if(checkIn.getId()!=null) {
                throw new CheckInBadArgException(String.format("Found unexpected id for checkin  %s", checkIn.getId()));
            } else if(memberId.equals(pdlId)) {
                throw new CheckInBadArgException(String.format("Team member id %s can't be same as PDL id", checkIn.getTeamMemberId()));
            } else if(!memberRepo.findById(memberId).isPresent()) {
                throw new CheckInBadArgException(String.format("Member %s doesn't exists", memberId));
            } else if(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId())) {
                throw new CheckInBadArgException(String.format("PDL %s is not associated with member %s", pdlId, memberId));
            } else if(chkInDate.isBefore(LocalDate.EPOCH) || chkInDate.isAfter(LocalDate.MAX)) {
                throw new CheckInBadArgException(String.format("Invalid date for checkin %s",memberId));
            }
            checkInRet = checkinRepo.save(checkIn);
        }

        return checkInRet ;
    }


    @Override
    public CheckIn read(@NotNull UUID id) {
        return checkinRepo.findById(id).orElse(null);
    }

    @Override
    public CheckIn update(CheckIn checkIn) {

        CheckIn checkInRet = null;

        if(checkIn!=null) {
            final UUID id = checkIn.getId();
            final UUID memberId = checkIn.getTeamMemberId();
            final UUID pdlId = checkIn.getPdlId();
            LocalDate chkInDate = checkIn.getCheckInDate();
            Boolean isAdmin = securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

            if(id==null||!checkinRepo.findById(id).isPresent()) {
                throw new CheckInBadArgException(String.format("Unable to find checkin record with id %s", checkIn.getId()));
            } else if(memberId==null) {
                throw new CheckInBadArgException(String.format("Invalid checkin %s", checkIn));
            } else if(!memberRepo.findById(memberId).isPresent()) {
                throw new CheckInBadArgException(String.format("Member %s doesn't exist", memberId));
            } else if(!pdlId.equals(memberRepo.findById(memberId).get().getPdlId())) {
                throw new CheckInBadArgException(String.format("PDL %s is not associated with member %s", pdlId, memberId));
            } else if(chkInDate.isBefore(LocalDate.EPOCH) || chkInDate.isAfter(LocalDate.MAX)) {
                throw new CheckInBadArgException(String.format("Invalid date for checkin %s",memberId));
            } else if(checkinRepo.findById(id).get().isCompleted() && !isAdmin) {
                throw new CheckInCompleteException(String.format("Checkin with id %s is complete and cannot be updated", checkIn.getId()));
            }

            checkInRet = checkinRepo.update(checkIn);
        }

        return checkInRet;
    }

    @Override
    public Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed) {
        Set<CheckIn> checkIn = new HashSet<>();
        checkinRepo.findAll().forEach(checkIn::add);
        if(teamMemberId!=null) {
            checkIn.retainAll(checkinRepo.findByTeamMemberId(teamMemberId));
        } else if(pdlId!=null) {
            checkIn.retainAll(checkinRepo.findByPdlId(pdlId));
        } else if(completed!=null) {
            checkIn.retainAll(checkinRepo.findByCompleted(completed));
        }
        return checkIn;
    }
    
}