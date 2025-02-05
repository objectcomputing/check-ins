package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class PrivateNoteServicesImpl implements PrivateNoteServices {

    private final CheckInServices checkinServices;
    private final PrivateNoteRepository privateNoteRepository;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public PrivateNoteServicesImpl(CheckInServices checkinServices,
                                   PrivateNoteRepository privateNoteRepository,
                                   MemberProfileServices memberProfileServices,
                                   CurrentUserServices currentUserServices) {
        this.checkinServices = checkinServices;
        this.privateNoteRepository = privateNoteRepository;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    @RequiredPermission(Permission.CAN_CREATE_PRIVATE_NOTE)
    public PrivateNote save(@NotNull PrivateNote privateNote) {
        validate(privateNote.getId() != null, "Found unexpected id %s for private note", privateNote.getId());

        final UUID createdById = privateNote.getCreatedbyid();
        validate(createdById == null, "Invalid private note %s, createdById null", privateNote);
        validate(memberProfileServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

        final UUID checkinId = privateNote.getCheckinid();
        validate(checkinId == null, "Invalid private note %s, checkinId null", privateNote);

        CheckIn checkinRecord = checkinServices.read(checkinId);
        validate(checkinRecord == null, "Checkin doesn't exits for given checkin Id");

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUserId);
        if (!canUpdateAllCheckins) {
            boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : false;
            boolean allowedToView = checkinServices.accessGranted(checkinRecord.getId(), currentUserId);
            if (!allowedToView || isCompleted ) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }

            boolean currentUserIsCheckinSubject = currentUserId.equals(checkinRecord.getTeamMemberId());
            if(currentUserIsCheckinSubject) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }

        }

        return privateNoteRepository.save(privateNote);
    }

    @Override
    @RequiredPermission(Permission.CAN_VIEW_PRIVATE_NOTE)
    public PrivateNote read(@NotNull UUID id) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();

        PrivateNote privateNoteResult = privateNoteRepository.findById(id).orElse(null);
        if (privateNoteResult == null) {
            throw new NotFoundException(String.format("Invalid private note id %s", id));
        }

        boolean canViewAllCheckins = checkinServices.canViewAllCheckins(currentUserId);
        if (!canViewAllCheckins) {
            CheckIn checkinRecord = checkinServices.read(privateNoteResult.getCheckinid());
            if (checkinRecord == null) {
                throw new NotFoundException(String.format("CheckIn %s doesn't exist", privateNoteResult.getCheckinid()));
            }

            if (!checkinServices.accessGranted(checkinRecord.getId(), currentUserId)) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }

            if(currentUserId.equals(checkinRecord.getTeamMemberId())) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
        }

        return privateNoteResult;
    }

    @Override
    @RequiredPermission(Permission.CAN_UPDATE_PRIVATE_NOTE)
    public PrivateNote update(@NotNull PrivateNote privateNote) {
        validate(privateNote.getId() == null, "No private note id %s found for updating", privateNote.getId());

        final UUID checkinId = privateNote.getCheckinid();
        validate(checkinId == null, "Invalid private note %s, checkinId null", privateNote);

        final UUID createdById = privateNote.getCreatedbyid();
        validate(createdById == null, "Invalid private note %s, createById null", privateNote);
        validate(memberProfileServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

        CheckIn checkinRecord = checkinServices.read(checkinId);
        validate(checkinRecord == null, "Checkin doesn't exits for given checkin Id");

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUserId);
        if (!canUpdateAllCheckins) {
            boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : false;
            boolean allowedToView = checkinServices.accessGranted(checkinRecord.getId(), currentUserId);

            if (!allowedToView || isCompleted ) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }

            boolean currentUserIsCheckinSubject = currentUserId.equals(checkinRecord.getTeamMemberId());
            if(currentUserIsCheckinSubject) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
        }

        return privateNoteRepository.update(privateNote);
    }

    @Override
    @RequiredPermission(Permission.CAN_VIEW_PRIVATE_NOTE)
    public Set<PrivateNote> findByFields(@Nullable UUID checkinId, @Nullable UUID createById) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if(!checkinServices.doesUserHaveViewAccess(currentUserId, checkinId, createById)){
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
        return privateNoteRepository.search(nullSafeUUIDToString(checkinId), nullSafeUUIDToString(createById));
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}
