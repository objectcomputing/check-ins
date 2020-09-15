package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInBadArgException;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;
import jnr.a64asm.Mem;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
public class CheckinNoteServicesImpl implements CheckinNoteServices {

    private CheckInRepository checkinRepo;
    private CheckinNoteRepository checkinNoteRepository;
    private MemberProfileRepository memberRepo;
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;

    public CheckinNoteServicesImpl(CheckInRepository checkinRepo, CheckinNoteRepository checkinNoteRepository,
                                   MemberProfileRepository memberRepo, SecurityService securityService,
                                   CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.checkinNoteRepository = checkinNoteRepository;
        this.memberRepo = memberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }


    @Override
    public CheckinNote save(CheckinNote checkinNote) {
        CheckinNote checkinNoteRet = null;
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (checkinNote != null) {
            final UUID checkinId = checkinNote.getCheckinid();
            final UUID createById = checkinNote.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted  = checkinRecord!=null?checkinRecord.isCompleted():null;
            final UUID pdlId = checkinRecord!=null?checkinRecord.getPdlId():null;
            if (checkinId == null || createById == null) {
                throw new CheckinNotesBadArgException(String.format("Invalid checkin note %s", checkinNote));
            } else if (checkinNote.getId() != null) {
                throw new CheckinNotesBadArgException(String.format("Found unexpected id %s for check in note", checkinNote.getId()));
            } else if (checkinRepo.findById(checkinId).isEmpty()) {
                throw new CheckinNotesBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (memberRepo.findById(createById).isEmpty()) {
                throw new CheckinNotesBadArgException(String.format("Member %s doesn't exist", createById));
            } else if(!isAdmin&&!isCompleted) {
                if(!currentUser.getId().equals(pdlId)) {
                    throw new CheckinNotesBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
                }
            }

            checkinNoteRet = checkinNoteRepository.save(checkinNote);
        }
        return checkinNoteRet;
    }

    @Override
    public CheckinNote read(@NotNull UUID id) {
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        CheckinNote checkInNoteResult =  checkinNoteRepository.findById(id).orElse(null);

        if(checkInNoteResult == null) {
            throw new CheckinNotesBadArgException(String.format("Invalid checkin note id %s",id));
        } else if(!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(checkInNoteResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord!=null?checkinRecord.getPdlId():null;
            final UUID createById = checkinRecord!=null?checkinRecord.getTeamMemberId():null;
            if(!currentUser.getId().equals(pdlId)&&!currentUser.getId().equals(createById)){
                throw new CheckinNotesBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
            }
        }
        return checkInNoteResult;
    }


    @Override
    public CheckinNote update(CheckinNote checkinNote) {
        CheckinNote checkinNoteRet = null;
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (checkinNote != null) {
            final UUID id = checkinNote.getId();
            final UUID checkinId = checkinNote.getCheckinid();
            final UUID createById = checkinNote.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted  = checkinRecord!=null ?checkinRecord.isCompleted():null;
            final UUID pdlId = checkinRecord!=null?checkinRecord.getPdlId():null;

            if (checkinId == null || createById == null) {
                throw new CheckinNotesBadArgException(String.format("Invalid checkin note %s", checkinNote));
            } else if (id == null || checkinNoteRepository.findById(id).isEmpty()) {
                throw new CheckinNotesBadArgException(String.format("Unable to locate checkin note to update with id %s", checkinNote.getId()));
            } else if (checkinRepo.findById(checkinId).isEmpty()) {
                throw new CheckinNotesBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (memberRepo.findById(createById).isEmpty()) {
                throw new CheckinNotesBadArgException(String.format("Member %s doesn't exist", createById));
            } else if(!isAdmin&&!isCompleted) {
                if(!currentUser.getId().equals(pdlId)) {
                    throw new CheckinNotesBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
                }
            }

            checkinNoteRet = checkinNoteRepository.update(checkinNote);
        }
        return checkinNoteRet;
    }


    @Override
    public Set<CheckinNote> findByFields(UUID checkinid, UUID createbyid) {
        Set<CheckinNote> checkinNote = new HashSet<>();
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        checkinNoteRepository.findAll().forEach(checkinNote::add);

            if(checkinid!=null) {
                CheckIn checkinRecord = checkinRepo.findById(checkinid).orElse(null);
                final UUID pdlId = checkinRecord!=null?checkinRecord.getPdlId():null;
                final UUID teamMemberId = checkinRecord!=null?checkinRecord.getTeamMemberId():null;
                if(!currentUser.getId().equals(pdlId)&&!currentUser.getId().equals(teamMemberId)&&!isAdmin){
                    throw new CheckinNotesBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
                } else {
                    checkinNote.retainAll(checkinNoteRepository.findByCheckinid(checkinid));
                }
            } else if(createbyid!=null) {
                MemberProfile memberRecord = memberRepo.findById(createbyid).orElse(null);

                if(!currentUser.getId().equals(memberRecord.getId())&&!isAdmin){
                    throw new CheckinNotesBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
                } else {
                    checkinNote.retainAll(checkinNoteRepository.findByCreatedbyid(createbyid));
                }
            } else if(!isAdmin) {
                throw new CheckinNotesBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
            }
        return checkinNote;
    }
}