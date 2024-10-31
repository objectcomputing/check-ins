package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class CheckinDocumentServicesImpl implements CheckinDocumentServices {

    private final CheckinDocumentRepository checkinDocumentRepo;
    private final CheckInRepository checkinRepo;

    public CheckinDocumentServicesImpl(CheckinDocumentRepository checkinDocumentRepo,
                                       CheckInRepository checkinRepo,
                                       CurrentUserServices currentUserServices) {
        this.checkinDocumentRepo = checkinDocumentRepo;
        this.checkinRepo = checkinRepo;
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
            throw new BadArgException(String.format("CheckinDocument with document id %s does not exist", uploadDocId));
        }
        return cd.get();
    }

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
            checkinDocumentRepo.deleteByUploadDocId(uploadDocId);
        }
    }
}
