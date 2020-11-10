package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

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
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (checkinNote != null) {
            final UUID checkinId = checkinNote.getCheckinid();
            final UUID createById = checkinNote.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid checkin note %s", checkinNote);
            validate(checkinNote.getId() != null, "Found unexpected id %s for check in note", checkinNote.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
            }

            checkinNoteRet = checkinNoteRepository.save(checkinNote);
        }
        return checkinNoteRet;
    }

    @Override
    public CheckinNote read(@NotNull UUID id) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        CheckinNote checkInNoteResult = checkinNoteRepository.findById(id).orElse(null);
        validate(checkInNoteResult == null, "Invalid checkin note id %s", id);
        if (!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(checkInNoteResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

        return checkInNoteResult;
    }


    @Override
    public CheckinNote update(CheckinNote checkinNote) {
        CheckinNote checkinNoteRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (checkinNote != null) {
            final UUID id = checkinNote.getId();
            final UUID checkinId = checkinNote.getCheckinid();
            final UUID createById = checkinNote.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid checkin note %s", checkinNote);
            validate(id == null || checkinNoteRepository.findById(id).isEmpty(), "Unable to locate checkin note to update with id %s", checkinNote.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
            }

            checkinNoteRet = checkinNoteRepository.update(checkinNote);
        }
        return checkinNoteRet;
    }


    @Override
    public Set<CheckinNote> findByFields(UUID checkinid, UUID createbyid) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (checkinid != null) {
            CheckIn checkinRecord = checkinRepo.findById(checkinid).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(teamMemberId) && !isAdmin, "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElse(null);
            validate(!currentUser.getId().equals(memberRecord.getId()) && !isAdmin, "User is unauthorized to do this operation");
        } else {
            validate(!isAdmin, "User is unauthorized to do this operation");
        }

        Set<CheckinNote> checkinNote = new HashSet<>(checkinNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid)));

        return checkinNote;
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new CheckinNotesBadArgException(String.format(message, args));
        }
    }
}