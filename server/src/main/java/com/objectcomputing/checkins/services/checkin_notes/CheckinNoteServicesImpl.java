package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class CheckinNoteServicesImpl implements CheckinNoteServices {

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
        boolean isAdmin = currentUserServices.isAdmin();

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

        validate(checkinId == null || createById == null, "Invalid checkin note %s", checkinNote);
        validate(checkinNote.getId() != null, "Found unexpected id %s for check in note", checkinNote.getId());
        validate(checkinRecord==null, "CheckIn %s doesn't exist", checkinId);
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
        validate(!createById.equals(checkinRecord.getTeamMemberId()) && !createById.equals(checkinRecord.getPdlId()), "User is unauthorized to do this operation");
        if (!isAdmin && isCompleted) {
            validate(true, "User is unauthorized to do this operation");
        }
        return checkinNoteRepository.save(checkinNote);
    }

    @Override
    public CheckinNote read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        CheckinNote checkInNoteResult = checkinNoteRepository.findById(id).orElse(null);

        validate(checkInNoteResult == null, "Invalid checkin note id %s", id);

        if (!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(checkInNoteResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

            if (!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return checkInNoteResult;
    }

    @Override
    public CheckinNote update(@NotNull CheckinNote checkinNote) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID id = checkinNote.getId();
        final UUID checkinId = checkinNote.getCheckinid();
        final UUID createById = checkinNote.getCreatedbyid();
        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

        if (checkinRecord!=null && !isAdmin && !currentUser.getId().equals(checkinRecord.getTeamMemberId()) && !currentUser.getId().equals(checkinRecord.getPdlId())) {
            throw new PermissionException("You do not have permission to access this resource");
        }

        validate(checkinId == null || createById == null, "Invalid checkin note %s", checkinNote);
        validate(id == null || checkinNoteRepository.findById(id).isEmpty(), "Unable to locate checkin note to update with id %s", checkinNote.getId());
        validate(checkinRecord==null, "CheckIn %s doesn't exist", checkinId);
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
        validate(!createById.equals(checkinRecord.getTeamMemberId()) && !createById.equals(checkinRecord.getPdlId()), "User is unauthorized to do this operation");
        if (!isAdmin && isCompleted) {
            validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
        }

        return checkinNoteRepository.update(checkinNote);
    }

    @Override
    public Set<CheckinNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (checkinid != null) {
            CheckIn checkinRecord = checkinRepo.findById(checkinid).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(teamMemberId) && !isAdmin, "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            validate(!currentUser.getId().equals(memberRecord.getId()) && !isAdmin, "User is unauthorized to do this operation");
        } else {
            validate(!isAdmin, "User is unauthorized to do this operation");
        }

        return checkinNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new CheckinNotesBadArgException(String.format(message, args));
        }
    }
}