package com.objectcomputing.checkins.services.file;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
import com.objectcomputing.checkins.services.FileServicesImplReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.CheckInDocumentFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.role.RoleType;

import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.MediaType;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;

import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;
import java.nio.ByteBuffer;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.fileservicesimpl", value = StringUtils.TRUE)
@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class FileServicesImplTest extends TestContainersSuite
                           implements MemberProfileFixture, CheckInFixture, CheckInDocumentFixture, RoleFixture {

    private class SimpleUploadFile implements CompletedFileUpload {
        String filename;

        public SimpleUploadFile(String name) {
            filename = name;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public long getDefinedSize() {
            return 50;
        }

        @Override
        public long getSize() {
            return getDefinedSize();
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String getName() {
            return getFilename();
        }

        @Override
        public Optional<MediaType> getContentType() {
            return Optional.of(MediaType.of(MediaType.TEXT_PLAIN));
        }

        @Override
        public byte[] getBytes() throws IOException {
            byte[] bytes = new byte[(int)getSize()];
            return bytes;
        }

        @Override
        public ByteBuffer getByteBuffer() throws IOException {
            return ByteBuffer.wrap(getBytes());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(getBytes());
        }
    }

    @Inject
    private CurrentUserServicesReplacement currentUserServices;

    // The bulk of the functionality for FileServicesImpl has been moved to
    // FileServicesBaseImpl, which this replacement extends.  The Google
    // related portion is the part that will not be tested (it was partially
    // tested previously with the use of Mockito).
    @Inject
    private FileServicesImplReplacement services;

    private CheckIn checkIn;
    private MemberProfile pdl;
    private MemberProfile member;

    @BeforeEach
    void reset() {
        services.reset();

        createAndAssignRoles();

        pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        member = createADefaultMemberProfileForPdl(pdl);
        assignMemberRole(member);

        currentUserServices.currentUser = createASecondDefaultMemberProfile();
        assignMemberRole(currentUserServices.currentUser);

        checkIn = createADefaultCheckIn(member, pdl);
    }

    @Test
    void testFindFilesForFindAll() {
        String fileId = "some.id";
        services.addFile(fileId, new byte[0]);

        assignAdminRole(currentUserServices.currentUser);

        final Set<FileInfoDTO> result = services.findFiles(null);

        assertNotNull(result);
        assertEquals(fileId, result.iterator().next().getFileId());
    }

    @Test
    void testFindAllFailsIfNotAdmin() {
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(null));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testFindFilesForFindByCheckinId() {
        String fileId = "some.id";
        services.addFile(fileId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, fileId);

        // Must be a PDL to even ask for the files from a check-in.
        currentUserServices.currentUser = pdl;
        final Set<FileInfoDTO> result = services.findFiles(checkIn.getId());

        assertNotNull(result);
        assertEquals(fileId, result.iterator().next().getFileId());
    }

    @Test
    void testFindByCheckinIdWhenNoDocsAreUploadedForCheckinId() {
        // Must be a PDL to even ask for the files from a check-in.
        currentUserServices.currentUser = pdl;
        final Set<FileInfoDTO> result = services.findFiles(checkIn.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCheckinIdForUnauthorizedUser() {
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(checkIn.getId()));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
    }

    @Test
    void testFindAllFilesThrowsException() {
        String fileId = "some.id";
        services.addFile(fileId, new byte[0]);

        assignAdminRole(currentUserServices.currentUser);

        services.shouldThrow = true;

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                () -> services.findFiles(null));
    }

    @Test
    void testFindByCheckinIdThrowsException() {
        String fileId = "some.id";
        services.addFile(fileId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, fileId);

        services.shouldThrow = true;

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                                                            () -> services.findFiles(checkIn.getId()));
    }

    @Test
    void testDownloadFiles() {
        String uploadDocId = "some.test.id";
        long byteCount = 50;
        services.addFile(uploadDocId, new byte[(int)byteCount]);

        currentUserServices.currentUser = pdl;
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        final java.io.File resultFile = services.downloadFiles(uploadDocId);
        assertEquals(byteCount, resultFile.length());
    }

    @Test
    void testDownloadFilesAdminCanAccess() {
        String uploadDocId = "some.test.id";
        long byteCount = 50;
        services.addFile(uploadDocId, new byte[(int)byteCount]);

        // The CheckInsServicesImpl checks with the rolePermissionServices.
        assignAdminRole(currentUserServices.currentUser);

        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);
        final java.io.File resultFile = services.downloadFiles(uploadDocId);

        assertEquals(byteCount, resultFile.length());
    }

    @Test
    void testDownloadFilesInvalidUploadDocId() {
        String invalidUploadDocId = "some.test.id";

        // This exception actually comes from the CheckinDocumentServices.
        final BadArgException responseException = assertThrows(BadArgException.class, () ->
                services.downloadFiles(invalidUploadDocId));

        assertEquals(String.format("CheckinDocument with document id %s does not exist", invalidUploadDocId), responseException.getMessage());
    }

    @Test
    void testDownloadFilesUnauthorizedUser() {
        String uploadDocId = "some.test.id";
        services.addFile(uploadDocId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        // This exception actually comes from the CheckinDocumentServices.
        final PermissionException responseException = assertThrows(PermissionException.class, () ->
                services.downloadFiles(uploadDocId));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testDownloadFilesThrowsException() {
        String uploadDocId = "some.test.id";
        services.addFile(uploadDocId, new byte[0]);

        currentUserServices.currentUser = pdl;
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        services.shouldThrow = true;

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                                                            () -> services.downloadFiles(uploadDocId));
    }

    @Test
    void testDeleteFile() {
        String uploadDocId = "Some.Upload.Doc.Id";
        services.addFile(uploadDocId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        currentUserServices.currentUser = pdl;
        Boolean result = services.deleteFile(uploadDocId);
        assertTrue(result);
    }

    @Test
    void testDeleteFilesAdminCanAccess() {
        String uploadDocId = "Some.Upload.Doc.Id";
        services.addFile(uploadDocId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        // The CheckInsServicesImpl checks with the rolePermissionServices.
        assignAdminRole(currentUserServices.currentUser);

        Boolean result = services.deleteFile(uploadDocId);
        assertTrue(result);
    }

    @Test
    void testDeleteFileWhenCheckinDocDoesntExist() {
        String uploadDocId = "Some.Upload.Doc.Id";

        // This exception actually comes from the CheckinDocumentServices.
        final BadArgException responseException = assertThrows(BadArgException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals(String.format("CheckinDocument with document id %s does not exist", uploadDocId), responseException.getMessage());
    }

    @Test
    void testDeleteFileByUnauthorizedUser() {
        String uploadDocId = "Some.Upload.Doc.Id";
        services.addFile(uploadDocId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        final PermissionException responseException = assertThrows(PermissionException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testDeleteFileThrowsException() {
        String uploadDocId = "Some.Upload.Doc.Id";
        services.addFile(uploadDocId, new byte[0]);
        CheckinDocument cd = createACustomCheckInDocument(checkIn, uploadDocId);

        services.shouldThrow = true;

        //act
        currentUserServices.currentUser = member;
        final PermissionException responseException = assertThrows(PermissionException.class,
                () -> services.deleteFile(uploadDocId));
    }

    @Test
    void testUploadFilesByCreatingNewDirectory() {
        CompletedFileUpload fileToUpload = new SimpleUploadFile("file.name");

        //act
        // Must be a PDL to upload files for a check-in.
        currentUserServices.currentUser = pdl;
        FileInfoDTO result = services.uploadFile(checkIn.getId(), fileToUpload);

        //assert
        assertNotNull(result);
        assertEquals(fileToUpload.getFilename(), result.getName());
    }

    @Test
    void testFileUploadForAdminByUploadingFileToExistingDirectory() {
        CompletedFileUpload fileToUpload = new SimpleUploadFile("file.name");

        // The CheckInsServicesImpl checks with the rolePermissionServices.
        assignAdminRole(currentUserServices.currentUser);

        //act
        FileInfoDTO result = services.uploadFile(checkIn.getId(), fileToUpload);

        //assert
        assertNotNull(result);
        assertEquals(fileToUpload.getFilename(), result.getName());
    }

    @Test
    void testUploadFileThrowsErrorWhenFileNameIsEmpty() {
        CompletedFileUpload fileToUpload = new SimpleUploadFile(null);

        currentUserServices.currentUser = member;
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(checkIn.getId(), fileToUpload));

        assertEquals("Please select a valid file before uploading.", responseException.getMessage());
    }

    @Test
    void testUploadFileThrowsErrorForInvalidCheckinId() {
        CompletedFileUpload fileToUpload = new SimpleUploadFile("file.name");
        UUID testCheckinId = UUID.randomUUID();

        // This exception actually comes from the CheckinDocumentServices.
        final NotFoundException responseException = assertThrows(NotFoundException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals(String.format("Checkin not found by Id: %s.", testCheckinId), responseException.getMessage());
    }

    @Test
    void testUploadFileUnauthorizedUser() {
        CompletedFileUpload fileToUpload = new SimpleUploadFile("file.name");

        final BadArgException responseException = assertThrows(BadArgException.class, () ->
                services.uploadFile(checkIn.getId(), fileToUpload));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testUploadFileThrowsErrorIfCheckinIsComplete() {
        CheckIn completed = createACompletedCheckIn(member, pdl);
        CompletedFileUpload fileToUpload = new SimpleUploadFile("file.name");

        currentUserServices.currentUser = member;
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(completed.getId(), fileToUpload));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testUploadFileThrowsException() {
        services.shouldThrow = true;
        CompletedFileUpload fileToUpload = new SimpleUploadFile("file.name");

        currentUserServices.currentUser = member;
        assertThrows(FileRetrievalException.class, () -> services.uploadFile(checkIn.getId(), fileToUpload));
    }
}
