package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class CheckinDocumentServicesImpl implements CheckinDocumentServices {

    private final CheckinDocumentRepository checkinDocumentRepo;
    private final CheckInRepository checkinRepo;
    private final CurrentUserServices currentUserServices;

    public CheckinDocumentServicesImpl(CheckinDocumentRepository checkinDocumentRepo,
                                       CheckInRepository checkinRepo,
                                       CurrentUserServices currentUserServices) {
        this.checkinDocumentRepo = checkinDocumentRepo;
        this.checkinRepo = checkinRepo;
        this.currentUserServices = currentUserServices;
    }

    @RequiredPermission(Permission.CAN_VIEW_CHECKIN_DOCUMENT)
    public Set<CheckinDocument> read(UUID checkinsId) {

        Set<CheckinDocument> checkinDocument = new HashSet<>();

        if (checkinsId != null) {
            checkinDocument = checkinDocumentRepo.findByCheckinsId(checkinsId);
        }
        return checkinDocument;
    }

    public CheckinDocument getFindByUploadDocId(@NotNull String uploadDocId) {
        Optional<CheckinDocument> cd = checkinDocumentRepo.findByUploadDocId(uploadDocId);
        if(cd.isEmpty()) {
            throw new BadArgException(String.format("CheckinDocument with document id %s does not exist", uploadDocId));
        }

        checkPermission(Permission.CAN_VIEW_CHECKIN_DOCUMENT,
                        cd.get().getCheckinsId());

        return cd.get();
    }

    @RequiredPermission(Permission.CAN_CREATE_CHECKIN_DOCUMENT)
    public CheckinDocument save(CheckinDocument checkinDocument) {

        CheckinDocument newCheckinDocument = null;

        if (checkinDocument != null) {
            if (checkinDocument.getCheckinsId() == null || checkinDocument.getUploadDocId() == null) {
                throw new BadArgException(String.format("Invalid CheckinDocument %s", checkinDocument));
            } else if (checkinDocument.getId() != null) {
                throw new BadArgException(String.format("Found unexpected CheckinDocument id %s, please try updating instead", checkinDocument.getId()));
            } else if (checkinRepo.findById(checkinDocument.getCheckinsId()).isEmpty()) {
                throw new BadArgException(String.format("CheckIn %s doesn't exist", checkinDocument.getCheckinsId()));
            } else if (checkinDocumentRepo.findByUploadDocId(checkinDocument.getUploadDocId()).isPresent()) {
                throw new BadArgException(String.format("CheckinDocument with document ID %s already exists", checkinDocument.getUploadDocId()));
            } else {
                newCheckinDocument = checkinDocumentRepo.save(checkinDocument);
            }
        }

        return newCheckinDocument;
    }

    @RequiredPermission(Permission.CAN_UPDATE_CHECKIN_DOCUMENT)
    public CheckinDocument update(CheckinDocument checkinDocument) {

        CheckinDocument updatedCheckinDocument = null;

        if (checkinDocument != null) {
            if (checkinDocument.getCheckinsId() == null || checkinDocument.getUploadDocId() == null) {
                throw new BadArgException(String.format("Invalid CheckinDocument %s", checkinDocument));
            } else if (checkinDocument.getId() == null || checkinDocumentRepo.findById(checkinDocument.getId()).isEmpty()) {
                throw new BadArgException(String.format("CheckinDocument id %s not found, please try inserting instead",
                        checkinDocument.getId()));
            } else if (checkinRepo.findById(checkinDocument.getCheckinsId()).isEmpty()) {
                throw new BadArgException(String.format("CheckIn %s doesn't exist", checkinDocument.getCheckinsId()));
            } else {
                updatedCheckinDocument = checkinDocumentRepo.update(checkinDocument);
            }
        }

        return updatedCheckinDocument;
    }

    @RequiredPermission(Permission.CAN_DELETE_CHECKIN_DOCUMENT)
    public void deleteByCheckinId(@NotNull UUID checkinsId) {

        if(!checkinDocumentRepo.existsByCheckinsId(checkinsId)) {
            throw new BadArgException(String.format("CheckinDocument with CheckinsId %s does not exist", checkinsId));
        } else {
            checkinDocumentRepo.deleteByCheckinsId(checkinsId);
        }
    }

    public void deleteByUploadDocId(@NotNull String uploadDocId) {

        if(!checkinDocumentRepo.existsByUploadDocId(uploadDocId)) {
            throw new BadArgException(String.format("CheckinDocument with uploadDocId %s does not exist", uploadDocId));
        } else {
            Optional<CheckinDocument> cd =
                checkinDocumentRepo.findByUploadDocId(uploadDocId);
            checkPermission(Permission.CAN_DELETE_CHECKIN_DOCUMENT,
                            cd.get().getCheckinsId());
            
            checkinDocumentRepo.deleteByUploadDocId(uploadDocId);
        }
    }

    private void checkPermission(Permission permission, UUID checkinId) {
        if (currentUserServices.hasPermission(permission)) {
            return;
        }

        Optional<CheckIn> checkIn = checkinRepo.findById(checkinId);
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        if (checkIn.isPresent() && currentUser != null &&
            checkIn.get().getPdlId().equals(currentUser.getId())) {
            return;
        }

        throw new PermissionException(NOT_AUTHORIZED_MSG);
    }
}
