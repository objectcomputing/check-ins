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
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean hasElevatedAccess = checkinServices.hasElevatedAccessPermission(currentUser.getId());

        final UUID checkinId = checkinNote.getCheckinid();
        final UUID createById = checkinNote.getCreatedbyid();
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;

        if (checkinRecord == null) {
            throw new NotFoundException(String.format("CheckIn %s doesn't exist", checkinId));
        }

        if (!checkinServices.accessGranted(checkinId, currentUser.getId())) {
            throw new PermissionException("You do not have permission to access this resource");
        }

        validate(createById == null, "Invalid checkin note %s", checkinNote);
        validate(checkinNote.getId() != null, "Found unexpected id %s for check in note", checkinNote.getId());
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
        if (!hasElevatedAccess && isCompleted) {
            validate(true, "User is unauthorized to do this operation");
        }
        return checkinNoteRepository.save(checkinNote);
    }

    @Override
    public CheckinNote read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean hasElevatedAccess = checkinServices.hasElevatedAccessPermission(currentUser.getId());
        CheckinNote checkInNoteResult = checkinNoteRepository.findById(id).orElse(null);

        validate(checkInNoteResult == null, "Invalid checkin note id %s", id);

        if (!hasElevatedAccess) {
            CheckIn checkinRecord = checkinRepo.findById(checkInNoteResult.getCheckinid()).orElse(null);
            if (checkinRecord == null) {
                throw new NotFoundException(String.format("CheckIn %s doesn't exist", checkInNoteResult.getCheckinid()));
            }

            if (!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return checkInNoteResult;
    }

    @Override
    public CheckinNote update(@NotNull CheckinNote checkinNote) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean hasElevatedAccess = checkinServices.hasElevatedAccessPermission(currentUser.getId());

        final UUID id = checkinNote.getId();
        final UUID checkinId = checkinNote.getCheckinid();
        final UUID createById = checkinNote.getCreatedbyid();
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

        validate(checkinRecord == null, "CheckIn %s doesn't exist", checkinId);
        if (!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId())) {
            LOG.debug("Access was not granted.");
            throw new PermissionException("User is unauthorized to do this operation");
        }
        validate(createById == null, "Invalid checkin note %s", checkinNote);
        validate(id == null || checkinNoteRepository.findById(id).isEmpty(), "Unable to locate checkin note to update with id %s", checkinNote.getId());
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);

        if (!hasElevatedAccess && isCompleted) {
            LOG.debug("User isn't admin and checkin is completed.");
            validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
        }

        return checkinNoteRepository.update(checkinNote);
    }

    @Override
    public Set<CheckinNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean hasElevatedAccess = checkinServices.hasElevatedAccessPermission(currentUser.getId());

        if (checkinid != null) {
            validate(!checkinServices.accessGranted(checkinid, currentUser.getId()), "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            validate(!currentUser.getId().equals(memberRecord.getId()) && !hasElevatedAccess, "User is unauthorized to do this operation");
        } else {
            validate(!hasElevatedAccess, "User is unauthorized to do this operation");
        }

        return checkinNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}