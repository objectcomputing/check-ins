package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.exceptions.BadArgException;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
            throw new BadArgException("CheckinDocument with document id %s does not exist", uploadDocId);
        }
        return cd.get();
    }

    public CheckinDocument save(CheckinDocument checkinDocument) {

        CheckinDocument newCheckinDocument = null;

        if (checkinDocument != null) {
            if (checkinDocument.getCheckinsId() == null || checkinDocument.getUploadDocId() == null) {
                throw new BadArgException("Invalid CheckinDocument %s", checkinDocument);
            } else if (checkinDocument.getId() != null) {
                throw new BadArgException("Found unexpected CheckinDocument id %s, please try updating instead", checkinDocument.getId());
            } else if (!checkinRepo.findById(checkinDocument.getCheckinsId()).isPresent()) {
                throw new BadArgException("CheckIn %s doesn't exist", checkinDocument.getCheckinsId());
            } else if (checkinDocumentRepo.findByUploadDocId(checkinDocument.getUploadDocId()).isPresent()) {
                throw new BadArgException("CheckinDocument with document ID %s already exists", checkinDocument.getUploadDocId());
            } else {
                newCheckinDocument = checkinDocumentRepo.save(checkinDocument);
            }
        }

        return newCheckinDocument;
    }

    public CheckinDocument update(CheckinDocument checkinDocument) {

        CheckinDocument updatedCheckinDocument = null;

        if (checkinDocument != null) {
            if (checkinDocument.getCheckinsId() == null || checkinDocument.getUploadDocId() == null) {
                throw new BadArgException("Invalid CheckinDocument %s", checkinDocument);
            } else if (checkinDocument.getId() == null || !checkinDocumentRepo.findById(checkinDocument.getId()).isPresent()) {
                throw new BadArgException("CheckinDocument id %s not found, please try inserting instead",
                        checkinDocument.getId());
            } else if (!checkinRepo.findById(checkinDocument.getCheckinsId()).isPresent()) {
                throw new BadArgException("CheckIn %s doesn't exist", checkinDocument.getCheckinsId());
            } else {
                updatedCheckinDocument = checkinDocumentRepo.update(checkinDocument);
            }
        }

        return updatedCheckinDocument;
    }

    public void deleteByCheckinId(@NotNull UUID checkinsId) {

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource");
        } else if(!checkinDocumentRepo.existsByCheckinsId(checkinsId)) {
            throw new BadArgException("CheckinDocument with CheckinsId %s does not exist", checkinsId);
        } else {
            checkinDocumentRepo.deleteByCheckinsId(checkinsId);
        }
    }

    public void deleteByUploadDocId(@NotNull String uploadDocId) {

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource");
        } else if(!checkinDocumentRepo.existsByUploadDocId(uploadDocId)) {
            throw new BadArgException("CheckinDocument with uploadDocId %s does not exist", uploadDocId);
        } else {
            checkinDocumentRepo.deleteByUploadDocId(uploadDocId);
        }
    }
}
