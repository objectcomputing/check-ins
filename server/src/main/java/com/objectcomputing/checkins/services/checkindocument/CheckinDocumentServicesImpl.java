package com.objectcomputing.checkins.services.checkindocument;

import io.micronaut.http.multipart.CompletedFileUpload;

import javax.inject.Inject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CheckinDocumentServicesImpl implements CheckinDocumentServices {

    @Inject
    private CheckinDocumentRepository checkinDocumentRepo;

    public Set<CheckinDocument> read(UUID checkinsId) {

        Set<CheckinDocument> checkinDocument = new HashSet<>();

        if (checkinsId != null) {
            checkinDocument = checkinDocumentRepo.findByCheckinsId(checkinsId);
        }
        return checkinDocument;
    }

    public CheckinDocument save(CheckinDocument checkinDocument) {

        CheckinDocument newCheckinDocument = null;

        if (checkinDocument != null) {
            if (checkinDocument.getId() != null) {
                throw new CheckinDocumentBadArgException(String.format("Found unexpected CheckinDocument id %s, please try updating instead",
                        checkinDocument.getId()));
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
            if (checkinDocument.getId() == null) {
                throw new CheckinDocumentBadArgException(String.format("CheckinDocument id %s not found, please try inserting instead",
                        checkinDocument.getId()));
            } else if (!checkinDocumentRepo.existsById(checkinDocument.getId())) {
                throw new CheckinDocumentBadArgException(String.format("CheckinDocument with ID %s does not exists", checkinDocument.getId()));
            } else {
                updatedCheckinDocument = checkinDocumentRepo.update(checkinDocument);
            }
        }

        return updatedCheckinDocument;
    }

    public void delete(UUID checkinsId) {

        if (checkinsId != null) {
            if(!checkinDocumentRepo.existsByCheckinsId(checkinsId)) {
                throw new CheckinDocumentBadArgException(String.format("CheckinDocument with CheckinsId %s does not exist", checkinsId));
            } else {
                checkinDocumentRepo.deleteByCheckinsId(checkinsId);
            }
        }
    }
}
