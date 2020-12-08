package com.objectcomputing.checkins.services.private_notes;

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
public class PrivateNoteServicesImpl implements PrivateNoteServices {

    private final CheckInRepository checkinRepo;
    private final PrivateNoteRepository privateNoteRepository;
    private final MemberProfileRepository memberRepo;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;

    public PrivateNoteServicesImpl(CheckInRepository checkinRepo, PrivateNoteRepository privateNoteRepository,
                                   MemberProfileRepository memberRepo, SecurityService securityService,
                                   CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.privateNoteRepository = privateNoteRepository;
        this.memberRepo = memberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public PrivateNote save(PrivateNote privateNote) {
        PrivateNote privateNoteRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (privateNote != null) {
            final UUID checkinId = privateNote.getCheckinid();
            final UUID createById = privateNote.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid checkin note %s", privateNote);
            validate(privateNote.getId() != null, "Found unexpected id %s for check in note", privateNote.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
            }

            privateNoteRet = privateNoteRepository.save(privateNote);
        }
        return privateNoteRet;
    }

    @Override
    public PrivateNote read(@NotNull UUID id) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        PrivateNote privateNoteResult = privateNoteRepository.findById(id).orElse(null);
        validate(privateNoteResult == null, "Invalid checkin note id %s", id);
        if (!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(privateNoteResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

        return privateNoteResult;
    }

    @Override
    public PrivateNote update(PrivateNote privateNote) {
        PrivateNote privateNoteRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (privateNote != null) {
            final UUID id = privateNote.getId();
            final UUID checkinId = privateNote.getCheckinid();
            final UUID createById = privateNote.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid checkin note %s", privateNote);
            validate(id == null || privateNoteRepository.findById(id).isEmpty(), "Unable to locate checkin note to update with id %s", privateNote.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
            }

            privateNoteRet = privateNoteRepository.update(privateNote);
        }
        return privateNoteRet;
    }

    @Override
    public Set<PrivateNote> findByFields(UUID checkinid, UUID createbyid) {
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

        Set<PrivateNote> privateNote = new HashSet<>(privateNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid)));

        return privateNote;
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new PrivateNotesBadArgException(String.format(message, args));
        }
    }
}