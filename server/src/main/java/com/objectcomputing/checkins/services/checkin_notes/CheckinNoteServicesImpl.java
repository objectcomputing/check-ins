package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class CheckinNoteServicesImpl implements CheckinNoteServices {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinNoteServicesImpl.class);

    private final CheckInRepository checkinRepo;
    private final CheckInServices checkinServices;
    private final CheckinNoteRepository checkinNoteRepository;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;

    public CheckinNoteServicesImpl(CheckInRepository checkinRepo, CheckInServices checkinServices, CheckinNoteRepository checkinNoteRepository,
                                   MemberProfileRepository memberRepo, CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.checkinServices = checkinServices;
        this.checkinNoteRepository = checkinNoteRepository;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public CheckinNote save(@NotNull CheckinNote checkinNote) {
        validate(checkinNote.getId() != null, "Found unexpected id %s for check in note", checkinNote.getId());
        
        final UUID createById = checkinNote.getCreatedbyid();
        validate(createById == null, "Invalid checkin note %s", checkinNote);
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
        
        final UUID checkinId = checkinNote.getCheckinid();
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
        if (checkinRecord == null) {
            throw new NotFoundException(String.format("CheckIn %s doesn't exist", checkinId));
        }

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean allowedToView = checkinServices.accessGranted(checkinId, currentUserId);
        if (!allowedToView) {
            throw new PermissionException("You do not have permission to access this resource");
        }

        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUserId);
        boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
        if (!canUpdateAllCheckins && isCompleted) {
            validate(true, "User is unauthorized to do this operation");
        }
        return checkinNoteRepository.save(checkinNote);
    }

    @Override
    public CheckinNote read(@NotNull UUID id) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        CheckinNote checkInNoteResult = checkinNoteRepository.findById(id).orElse(null);
        validate(checkInNoteResult == null, "Invalid checkin note id %s", id);

        CheckIn checkinRecord = checkinRepo.findById(checkInNoteResult.getCheckinid()).orElse(null);
        if (checkinRecord == null) {
            throw new NotFoundException(String.format("CheckIn %s doesn't exist", checkInNoteResult.getCheckinid()));
        }

        boolean allowedToView = checkinServices.accessGranted(checkinRecord.getId(), currentUserId);
        if (!allowedToView) {
            throw new PermissionException("User is unauthorized to do this operation");
        }

        return checkInNoteResult;
    }

    @Override
    public CheckinNote update(@NotNull CheckinNote checkinNote) {
        final UUID id = checkinNote.getId();
        validate(id == null || checkinNoteRepository.findById(id).isEmpty(), "Unable to locate checkin note to update with id %s", checkinNote.getId());

        final UUID createById = checkinNote.getCreatedbyid();
        validate(createById == null, "Invalid checkin note %s", checkinNote);
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);

        final UUID checkinId = checkinNote.getCheckinid();
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
        validate(checkinRecord == null, "CheckIn %s doesn't exist", checkinId);

        final UUID checkinRecordId = checkinRecord != null ? checkinRecord.getId() : null;
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        
        boolean allowedToView = checkinServices.accessGranted(checkinRecordId, currentUserId);
        if (!allowedToView) {
            LOG.debug("Access was not granted.");
            throw new PermissionException("User is unauthorized to do this operation");
        }

        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUserId);
        boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
        if (!canUpdateAllCheckins && isCompleted) {
            LOG.debug("User isn't admin and checkin is completed.");
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(!currentUserId.equals(pdlId), "User is unauthorized to do this operation");
        }

        return checkinNoteRepository.update(checkinNote);
    }

    @Override
    public Set<CheckinNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canViewAllCheckins = checkinServices.canViewAllCheckins(currentUserId);

        if (checkinid != null) {
            validate(!checkinServices.accessGranted(checkinid, currentUserId), "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            validate(!currentUserId.equals(memberRecord.getId()) && !canViewAllCheckins, "User is unauthorized to do this operation");
        } else {
            validate(!canViewAllCheckins, "User is unauthorized to do this operation");
        }

        return checkinNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}