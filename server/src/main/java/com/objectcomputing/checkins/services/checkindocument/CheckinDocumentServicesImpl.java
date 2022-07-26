package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.exceptions.BadArgException;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.Validation.validate;

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
        return checkinDocumentRepo.findByUploadDocId(uploadDocId).orElseThrow(() -> {
            throw new NotFoundException("CheckinDocument with document id %s does not exist", uploadDocId);
        });
    }

    public CheckinDocument save(CheckinDocument checkinDocument) {

        CheckinDocument newCheckinDocument = null;

        if (checkinDocument != null) {

            validate(checkinDocument.getCheckinsId() != null && checkinDocument.getUploadDocId() != null).orElseThrow(() -> {
                throw new BadArgException("Invalid CheckinDocument %s", checkinDocument);
            });
            validate(checkinDocument.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected CheckinDocument id %s, please try updating instead", checkinDocument.getId());
            });
            checkinRepo.findById(checkinDocument.getCheckinsId()).orElseThrow(() -> {
                throw new BadArgException("CheckIn %s doesn't exist", checkinDocument.getCheckinsId());
            });
            validate(checkinDocumentRepo.findByUploadDocId(checkinDocument.getUploadDocId()).isEmpty()).orElseThrow(() -> {
                throw new AlreadyExistsException("CheckinDocument with document ID %s already exists", checkinDocument.getUploadDocId());
            });

            newCheckinDocument = checkinDocumentRepo.save(checkinDocument);
        }

        return newCheckinDocument;
    }

    public CheckinDocument update(CheckinDocument checkinDocument) {

        CheckinDocument updatedCheckinDocument = null;

        if (checkinDocument != null) {

            validate(checkinDocument.getCheckinsId() != null && checkinDocument.getUploadDocId() != null).orElseThrow(() -> {
                throw new BadArgException("Invalid CheckinDocument %s", checkinDocument);
            });
            validate(checkinDocument.getId() != null && checkinDocumentRepo.findById(checkinDocument.getId()).isPresent()).orElseThrow(() -> {
                throw new BadArgException("CheckinDocument id %s not found, please try inserting instead", checkinDocument.getId());
            });
            checkinRepo.findById(checkinDocument.getCheckinsId()).orElseThrow(() -> {
                throw new BadArgException("CheckIn %s doesn't exist", checkinDocument.getCheckinsId());
            });

            updatedCheckinDocument = checkinDocumentRepo.update(checkinDocument);
        }

        return updatedCheckinDocument;
    }

    public void deleteByCheckinId(@NotNull UUID checkinsId) {

        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });
        validate(checkinDocumentRepo.existsByCheckinsId(checkinsId)).orElseThrow(() -> {
            throw new BadArgException("CheckinDocument with CheckinsId %s does not exist", checkinsId);
        });

        checkinDocumentRepo.deleteByCheckinsId(checkinsId);
    }

    public void deleteByUploadDocId(@NotNull String uploadDocId) {

        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });
        validate(checkinDocumentRepo.existsByUploadDocId(uploadDocId)).orElseThrow(() -> {
            throw new BadArgException("CheckinDocument with uploadDocId %s does not exist", uploadDocId);
        });

        checkinDocumentRepo.deleteByUploadDocId(uploadDocId);
    }
}
