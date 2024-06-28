package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
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
    // todo remove manual validations throughout class in favor of jakarta validations at api level.

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
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUserId);
        boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
        if (!canUpdateAllCheckins && isCompleted) {
            validate(true, NOT_AUTHORIZED_MSG);
        }
        LOG.info("Saving new checkinNote");
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
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
        LOG.info("Found checkin note with id {}", id);
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
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        boolean canUpdateAllCheckins = checkinServices.canUpdateAllCheckins(currentUserId);
        boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
        if (!canUpdateAllCheckins && isCompleted) {
            LOG.debug("User isn't admin and checkin is completed.");
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(!currentUserId.equals(pdlId), NOT_AUTHORIZED_MSG);
        }

        LOG.info("Updating checkinNote {}", checkinNote.getId());
        return checkinNoteRepository.update(checkinNote);
    }

    @Override
    public Set<CheckinNote> findByFields(@Nullable UUID checkinId, @Nullable UUID createById) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if(!checkinServices.doesUserHaveViewAccess(currentUserId, checkinId, createById)){
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
        LOG.info("Finding AgendaItem by checkinId: {}, and createById: {}", checkinId, createById);
        return checkinNoteRepository.search(nullSafeUUIDToString(checkinId), nullSafeUUIDToString(createById));
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}