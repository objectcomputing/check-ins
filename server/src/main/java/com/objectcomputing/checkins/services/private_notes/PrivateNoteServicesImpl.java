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
                throw new PermissionException("User is unauthorized to do this operation");
            }

            boolean currentUserIsCheckinSubject = currentUserId.equals(checkinRecord.getTeamMemberId());
            if(currentUserIsCheckinSubject) {
                throw new PermissionException("User is unauthorized to do this operation");
            }

        }

        return privateNoteRepository.save(privateNote);
    }

    @Override
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
                throw new PermissionException("User is unauthorized to do this operation");
            }

            if(currentUserId.equals(checkinRecord.getTeamMemberId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return privateNoteResult;
    }

    @Override
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
                throw new PermissionException("User is unauthorized to do this operation");
            }

            boolean currentUserIsCheckinSubject = currentUserId.equals(checkinRecord.getTeamMemberId());
            if(currentUserIsCheckinSubject) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return privateNoteRepository.update(privateNote);
    }

    @Override
    public Set<PrivateNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canViewAllCheckins = checkinServices.canViewAllCheckins(currentUserId);

        if (checkinid != null) {
            if (!checkinServices.accessGranted(checkinid, currentUserId))
                throw new PermissionException("User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            if (!currentUserId.equals(memberRecord.getId()) && !canViewAllCheckins)
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
