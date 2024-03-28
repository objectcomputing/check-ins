package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.exceptions.BadArgException;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class PrivateNoteServicesImpl implements PrivateNoteServices {

    private final CheckInServices checkinServices;
    private final PrivateNoteRepository privateNoteRepository;
    private final MemberProfileRepository memberRepo;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;
    final String unauthorizedErrorMessage ="User is unauthorized to do this operation";

    public PrivateNoteServicesImpl(CheckInServices checkinServices, CheckInRepository checkinRepo, PrivateNoteRepository privateNoteRepository,
                                   MemberProfileRepository memberRepo, MemberProfileServices memberProfileServices,
                                   CurrentUserServices currentUserServices) {
        this.checkinServices = checkinServices;
        this.privateNoteRepository = privateNoteRepository;
        this.memberRepo = memberRepo;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public PrivateNote save(@NotNull PrivateNote privateNote) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUser.getId());


        final UUID checkinId = privateNote.getCheckinid();
        final UUID createdById = privateNote.getCreatedbyid();
        CheckIn checkinRecord = checkinServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : false;

        validate(privateNote.getId() != null, "Found unexpected id %s for private note", privateNote.getId());
        validate(checkinId == null || createdById == null, "Invalid private note %s", privateNote);
        validate(checkinRecord == null, "Checkin doesn't exits for given checkin Id");
        validate(memberProfileServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

        if (!canUpdateAllCheckins) {

            if (!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId()) || isCompleted ) {
                throw new PermissionException("User is unauthorized to do this operation");
            }

            if(currentUser.getId().equals(checkinRecord.getTeamMemberId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }

        }

        return privateNoteRepository.save(privateNote);
    }

    @Override
    public PrivateNote read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canViewAllCheckins = checkinServices.canViewAllCheckins(currentUser.getId());
        PrivateNote privateNoteResult = privateNoteRepository.findById(id).orElse(null);

        if (privateNoteResult == null) {
            throw new NotFoundException(String.format("Invalid private note id %s", id));
        }

        if (!canViewAllCheckins) {
            CheckIn checkinRecord = checkinServices.read(privateNoteResult.getCheckinid());
            if (checkinRecord == null) {
                throw new NotFoundException(String.format("CheckIn %s doesn't exist", privateNoteResult.getCheckinid()));
            }

            if (!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }

            if(currentUser.getId().equals(checkinRecord.getTeamMemberId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return privateNoteResult;
    }

    @Override
    public PrivateNote update(@NotNull PrivateNote privateNote) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUser.getId());
        Boolean isPdl = currentUserServices.hasRole(RoleType.PDL);

        final UUID checkinId = privateNote.getCheckinid();
        final UUID createdById = privateNote.getCreatedbyid();
        CheckIn checkinRecord = checkinServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : false;

        validate(checkinId == null || createdById == null, "Invalid private note %s", privateNote);
        validate((canUpdateAllCheckins && !isPdl) || isCompleted , unauthorizedErrorMessage);
        validate(privateNote.getId() == null, "No private note id %s found for updating", privateNote.getId());
        validate(checkinRecord == null, "Checkin doesn't exits for given checkin Id");
        validate(memberProfileServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

        if (!canUpdateAllCheckins) {

            if (!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId()) || isCompleted ) {
                throw new PermissionException("User is unauthorized to do this operation");
            }

            if(currentUser.getId().equals(checkinRecord.getTeamMemberId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return privateNoteRepository.update(privateNote);

    }

    @Override
    public Set<PrivateNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canViewAllCheckins = checkinServices.canViewAllCheckins(currentUser.getId());

        if (checkinid != null) {
            if (!checkinServices.accessGranted(checkinid, currentUser.getId()))
                throw new PermissionException("User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            if (!currentUser.getId().equals(memberRecord.getId()) && !canViewAllCheckins)
                throw new PermissionException("User is unauthorized to do this operation");
        } else if (!canViewAllCheckins) {
            throw new PermissionException("User is unauthorized to do this operation");
        }

        return privateNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}
