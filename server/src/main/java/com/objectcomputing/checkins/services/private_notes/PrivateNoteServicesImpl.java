package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Singleton
public class PrivateNoteServicesImpl implements PrivateNoteServices {

    private final CheckInServices checkinServices;
    private final PrivateNoteRepository privateNoteRepository;
    private final MemberProfileServices memberProfileServices;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;

    public PrivateNoteServicesImpl(CheckInServices checkinServices, PrivateNoteRepository privateNoteRepository,
                                   MemberProfileServices memberProfileServices, SecurityService securityService,
                                   CurrentUserServices currentUserServices) {
        this.checkinServices = checkinServices;
        this.privateNoteRepository = privateNoteRepository;
        this.memberProfileServices = memberProfileServices;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public PrivateNote save(@NotNull PrivateNote privateNote) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = currentUserServices.isAdmin();
        Boolean isPdl = currentUserServices.hasRole(RoleType.PDL);

        final UUID checkinId = privateNote.getCheckinid();
        final UUID createdById = privateNote.getCreatedbyid();
        CheckIn checkinRecord = checkinServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;

        validate(privateNote.getId() != null, "Found unexpected id %s for private note", privateNote.getId());
        validate(checkinId == null || createdById == null, "Invalid checkin note %s", privateNote);
        validate(checkinRecord.equals(null), "Checkin doesn't exits for given checkin Id");
        validate(memberProfileServices.getById(createdById).equals(null), "Member %s doesn't exist", createdById);
        validate((isAdmin && !isPdl) || isCompleted , "User1 is unauthorized to do this operation");
        validate(!currentUser.getId().equals(createdById), "User2 is unauthorized to do this operation");
        validate((!currentUser.getId().equals(checkinRecord.getTeamMemberId()) && !currentUser.getId().equals(checkinRecord.getPdlId())), "User3 is unauthorized to do this operation");
        return privateNoteRepository.save(privateNote);
    }

    @Override
    public PrivateNote read(@NotNull UUID id) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = currentUserServices.isAdmin();
        PrivateNote privateNoteResult = privateNoteRepository.findById(id).orElse(null);
        validate(privateNoteResult == null, "Invalid private note id %s", id);
        CheckIn checkinRecord = checkinServices.read(privateNoteResult.getCheckinid());
        validate(isAdmin && !privateNoteResult.getCreatedbyid().equals(checkinRecord.getPdlId()),"Private note is created by Member and Admin is not authorized to read");
        validate(!isAdmin && !currentUser.getId().equals(privateNoteResult.getCreatedbyid()), "User is unauthorized to do this operation");
        return privateNoteResult;
    }

    @Override
    public PrivateNote update(@NotNull PrivateNote privateNote) {
        PrivateNote privateNoteRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        Boolean isPdl = currentUserServices.hasRole(RoleType.PDL);

        final UUID id = privateNote.getId();
        final UUID checkinId = privateNote.getCheckinid();
        final UUID createdById = privateNote.getCreatedbyid();
        CheckIn checkinRecord = checkinServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;

        validate(checkinId == null || createdById == null, "Invalid private note %s", privateNote);
        validate((isAdmin && !isPdl) || isCompleted , "User is unauthorized to do this operation");
        validate(privateNote.getId().equals(null), "No private note id %s found for updating", privateNote.getId());
        validate(checkinRecord.equals(null), "Checkin doesn't exits for given checkin Id");
        validate(memberProfileServices.getById(createdById).equals(null), "Member %s doesn't exist", createdById);
        validate(!currentUser.getId().equals(createdById), "User is unauthorized to do this operation");
        validate((!currentUser.getId().equals(checkinRecord.getTeamMemberId()) && !currentUser.getId().equals(checkinRecord.getPdlId())), "User is unauthorized to do this operation");
        return privateNoteRepository.update(privateNote);

    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new PrivateNotesBadArgException(String.format(message, args));
        }
    }
}
