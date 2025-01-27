package com.objectcomputing.checkins.services.file;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.multipart.CompletedFileUpload;

import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
abstract public class FileServicesBaseImpl implements FileServices {
    private static final Logger LOG = LoggerFactory.getLogger(FileServicesBaseImpl.class);

    protected final CheckInServices checkInServices;
    protected final CheckinDocumentServices checkinDocumentServices;
    protected final MemberProfileServices memberProfileServices;
    protected final CurrentUserServices currentUserServices;

    public FileServicesBaseImpl(CheckInServices checkInServices,
                                CheckinDocumentServices checkinDocumentServices,
                                MemberProfileServices memberProfileServices,
                                CurrentUserServices currentUserServices) {
        this.checkInServices = checkInServices;
        this.checkinDocumentServices = checkinDocumentServices;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    abstract protected void getCheckinDocuments(
        Set<FileInfoDTO> result, Set<CheckinDocument> checkinDocuments) throws IOException;
    abstract protected void downloadSingleFile(
        String docId, FileOutputStream myWriter) throws IOException;
    abstract protected FileInfoDTO uploadSingleFile(
        CompletedFileUpload file, String directoryName,
        Function<String, CheckinDocument> consumer) throws IOException;
    abstract protected void deleteSingleFile(String docId) throws IOException;

    @Override
    public Set<FileInfoDTO> findFiles(@Nullable UUID checkInID) {
        boolean canAdminister = hasAdministerPermission();
        validate(checkInID == null && !canAdminister, NOT_AUTHORIZED_MSG);

        try {
            Set<FileInfoDTO> result = new HashSet<>();
            if (checkInID == null && canAdminister) {
                getCheckinDocuments(result, Collections.emptySet());
            } else if (checkInID != null) {
                validate(!checkInServices.accessGranted(checkInID, currentUserServices.getCurrentUser().getId()),
                        "You are not authorized to perform this operation");

                // If there aren't any documents, do not call
                // getCheckinDocument.  It assumes that an empty set means
                // that it should attempt to get all documents.  And, in this
                // case, we just want an empty result set.
                Set<CheckinDocument> checkinDocuments = checkinDocumentServices.read(checkInID);
                if (!checkinDocuments.isEmpty()) {
                    getCheckinDocuments(result, checkinDocuments);
                }
            }

            return result;
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public java.io.File downloadFiles(@NotNull String uploadDocId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canAdminister = hasAdministerPermission();

        CheckinDocument cd = checkinDocumentServices.getFindByUploadDocId(uploadDocId);
        validate(cd == null, String.format("Unable to find record with id %s", uploadDocId));

        CheckIn associatedCheckin = checkInServices.read(cd.getCheckinsId());

        if(!canAdminister) {
            validate((!currentUser.getId().equals(associatedCheckin.getTeamMemberId()) && !currentUser.getId().equals(associatedCheckin.getPdlId())), NOT_AUTHORIZED_MSG);
        }
        try {
            java.io.File file = java.io.File.createTempFile("tmp", ".txt");
            file.deleteOnExit();
            try(
                FileOutputStream myWriter = new FileOutputStream(file)
            ) {
                downloadSingleFile(uploadDocId, myWriter);
                return file;
            } catch (IOException e) {
                LOG.error("Error occurred while retrieving files.", e);
                throw new FileRetrievalException(e.getMessage());
            }
        } catch(IOException e) {
            LOG.error("Error occurred while attempting to create a temporary file.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public FileInfoDTO uploadFile(@NotNull UUID checkInID, @NotNull CompletedFileUpload file) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canAdminister = hasAdministerPermission();
        validate((file.getFilename() == null || file.getFilename().equals("")), "Please select a valid file before uploading.");

        CheckIn checkIn = checkInServices.read(checkInID);
        validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);
        if(!canAdminister) {
            validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
            validate(checkIn.isCompleted(), NOT_AUTHORIZED_MSG);
        }

        // create folder for each team member
        final String directoryName = MemberProfileUtils.getFullName(memberProfileServices.getById(checkIn.getTeamMemberId()));

        try {
            return uploadSingleFile(file, directoryName,
                                    (fileId) -> {
                //create record in checkin-document service
                CheckinDocument cd = new CheckinDocument(checkInID, fileId);
                checkinDocumentServices.save(cd);
                return cd;
            });
        } catch (IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(@NotNull String uploadDocId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canDelete = currentUserServices.hasPermission(Permission.CAN_DELETE_CHECKIN_DOCUMENT);

        CheckinDocument cd = checkinDocumentServices.getFindByUploadDocId(uploadDocId);
        validate(cd == null, String.format("Unable to find record with id %s", uploadDocId));

        CheckIn associatedCheckin = checkInServices.read(cd.getCheckinsId());
        if(!canDelete) {
            validate((!currentUser.getId().equals(associatedCheckin.getTeamMemberId()) && !currentUser.getId().equals(associatedCheckin.getPdlId())), NOT_AUTHORIZED_MSG);
        }

        try {
            deleteSingleFile(uploadDocId);
            checkinDocumentServices.deleteByUploadDocId(uploadDocId);
            return true;
        } catch (IOException e) {
            LOG.error("Error occurred while deleting files.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    protected void validate(boolean isError, String message, Object... args) {
        if(isError) {
            throw new FileRetrievalException(String.format(message, args));
        }
    }

    protected boolean hasAdministerPermission() {
        return currentUserServices.hasPermission(Permission.CAN_ADMINISTER_CHECKIN_DOCUMENTS);
    }
}
