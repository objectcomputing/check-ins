package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class CheckinDocumentServicesImpl implements CheckinDocumentServices {

    @Inject
    private CheckinDocumentRepository checkinDocumentRepo;

    @Inject
    private CheckInRepository checkinRepo;

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
            throw new CheckinDocumentBadArgException(String.format("CheckinDocument with document id %s does not exist", uploadDocId));
        }
        return cd.get();
    }

    public CheckinDocument save(CheckinDocument checkinDocument) {

        CheckinDocument newCheckinDocument = null;

        if (checkinDocument != null) {
            if (checkinDocument.getCheckinsId() == null || checkinDocument.getUploadDocId() == null) {
                throw new CheckinDocumentBadArgException(String.format("Invalid CheckinDocument %s", checkinDocument));
            } else if (checkinDocument.getId() != null) {
                throw new CheckinDocumentBadArgException(String.format("Found unexpected CheckinDocument id %s, please try updating instead", checkinDocument.getId()));
            } else if (!checkinRepo.findById(checkinDocument.getCheckinsId()).isPresent()) {
                throw new CheckinDocumentBadArgException(String.format("CheckIn %s doesn't exist", checkinDocument.getCheckinsId()));
            } else if (checkinDocumentRepo.findByUploadDocId(checkinDocument.getUploadDocId()).isPresent()) {
                throw new CheckinDocumentBadArgException(String.format("CheckinDocument with document ID %s already exists", checkinDocument.getUploadDocId()));
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
                throw new CheckinDocumentBadArgException(String.format("Invalid CheckinDocument %s", checkinDocument));
            } else if (checkinDocument.getId() == null || !checkinDocumentRepo.findById(checkinDocument.getId()).isPresent()) {
                throw new CheckinDocumentBadArgException(String.format("CheckinDocument id %s not found, please try inserting instead",
                        checkinDocument.getId()));
            } else if (!checkinRepo.findById(checkinDocument.getCheckinsId()).isPresent()) {
                throw new CheckinDocumentBadArgException(String.format("CheckIn %s doesn't exist", checkinDocument.getCheckinsId()));
            } else {
                updatedCheckinDocument = checkinDocumentRepo.update(checkinDocument);
            }
        }

        return updatedCheckinDocument;
    }

    public void deleteByCheckinId(@NotNull UUID checkinsId) {

        if(!checkinDocumentRepo.existsByCheckinsId(checkinsId)) {
            throw new CheckinDocumentBadArgException(String.format("CheckinDocument with CheckinsId %s does not exist", checkinsId));
        } else {
            checkinDocumentRepo.deleteByCheckinsId(checkinsId);
        }
    }

    public void deleteByUploadDocId(@NotNull String uploadDocId) {

        if(!checkinDocumentRepo.existsByUploadDocId(uploadDocId)) {
            throw new CheckinDocumentBadArgException(String.format("CheckinDocument with uploadDocId %s does not exist", uploadDocId));
        } else {
            checkinDocumentRepo.deleteByUploadDocId(uploadDocId);
        }
    }
}
