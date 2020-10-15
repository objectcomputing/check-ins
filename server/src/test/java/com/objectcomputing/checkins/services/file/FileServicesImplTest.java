package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.testing.json.GoogleJsonResponseExceptionFactoryTesting;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.common.io.ByteStreams;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileServicesImplTest {

    private static File testFile;
    private final static String filePath = "testFile.txt";
    final static Authentication authentication = mock(Authentication.class);
    final static Map mockAttributes = mock(Map.class);
    final static Drive drive = mock(Drive.class);
    final static Drive.Files files = mock(Drive.Files.class);
    final static Drive.Files.List list = mock(Drive.Files.List.class);
    final static Drive.Files.Get get = mock(Drive.Files.Get.class);
    final static Drive.Files.Delete delete = mock(Drive.Files.Delete.class);
    final static Drive.Files.Create create = mock(Drive.Files.Create.class);
    final static CompletedFileUpload fileToUpload = mock(CompletedFileUpload.class);
    final static MemberProfile testMemberProfile = mock(MemberProfile.class);
    final static CheckIn testCheckIn = mock(CheckIn.class);
    final static CheckinDocument testCd = mock(CheckinDocument.class);
    final static InputStream mockInputStream = mock(InputStream.class);
    final static JsonFactory jsonFactory = new MockJsonFactory();

    @Mock
    private CheckInServices checkInServices;

    @Mock
    private CheckinDocumentServices checkinDocumentServices;

    @Mock
    private GoogleDriveAccessor googleDriveAccessor;

    @Mock
    private SecurityService securityService;

    @Mock
    private CurrentUserServices currentUserServices;

    @Mock
    private MemberProfileServices memberProfileServices;

    @Mock
    private CompletedFileUpload completedFileUpload;

    @InjectMocks
    private FileServicesImpl services;

    @BeforeAll
    void initMocksAndInitializeFile() throws IOException {
        MockitoAnnotations.initMocks(this);

        testFile = new File(filePath);
        FileWriter myWriter = new FileWriter(testFile);
        myWriter.write("This.Is.A.Test.File");
        myWriter.close();
    }

    @AfterAll
    void deleteTestFile() {
        testFile.deleteOnExit();
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(checkInServices);
        Mockito.reset(checkinDocumentServices);
        Mockito.reset(googleDriveAccessor);
        Mockito.reset(securityService);
        Mockito.reset(currentUserServices);
        Mockito.reset(memberProfileServices);
        Mockito.reset(completedFileUpload);
    }

    private void setupMocksForAuth() {
        when(securityService.getAuthentication()).thenReturn(Optional.of(authentication));
        when(authentication.getAttributes()).thenReturn(mockAttributes);
        when(mockAttributes.get("email")).thenReturn(mockAttributes);
        when(mockAttributes.toString()).thenReturn("test.email");
        when(currentUserServices.findOrSaveUser(any(), any())).thenReturn(testMemberProfile);
    }

    @Test
    void testFindFilesForFindAll() throws IOException {

        FileList fileList = new FileList();
        List<com.google.api.services.drive.model.File> mockFiles = new ArrayList<>();
        com.google.api.services.drive.model.File file1 = new com.google.api.services.drive.model.File();
        file1.setId("some.id");
        mockFiles.add(file1);
        fileList.setFiles(mockFiles);

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.execute()).thenReturn(fileList);

        final HttpResponse<?> response = services.findFiles(null);
        Set<FileInfoDTO> result = (Set<FileInfoDTO>) response.getBody().get();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(fileList.getFiles().iterator().next().getId(), result.iterator().next().getFileId());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).read(any(UUID.class));
        verify(checkInServices, times(0)).read(any(UUID.class));
    }

    @Test
    void testFindAllFailsIfNotAdmin() {
        setupMocksForAuth();
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(null));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
    }

    @Test
    void testFindFilesForFindByCheckinId() throws IOException {

        com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
        file.setId("some.id");

        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberProfileId = UUID.randomUUID();
        final Set<CheckinDocument> testCheckinDocument = new HashSet<>();
        testCheckinDocument.add(testCd);

        setupMocksForAuth();
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(any(String.class))).thenReturn(get);
        when(get.execute()).thenReturn(file);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(checkinDocumentServices.read(testCheckinId)).thenReturn(testCheckinDocument);
        when(testCd.getUploadDocId()).thenReturn("some.upload.doc.id");

        final HttpResponse<?> response = services.findFiles(testCheckinId);
        Set<FileInfoDTO> result = (Set<FileInfoDTO>) response.getBody().get();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(file.getId(), result.iterator().next().getFileId());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).read(testCheckinId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testFindByCheckinIdThrowsErrorIfCheckinIdIsInvalid() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(checkInServices.read(testCheckinId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(testCheckinId));

        assertEquals(String.format("Unable to find checkin record with id %s", testCheckinId), responseException.getMessage());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).read(any(UUID.class));
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testFindByCheckinIdWhenNoDocsAreUploadedForCheckinId() throws IOException {

        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberProfileId = UUID.randomUUID();

        setupMocksForAuth();
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(checkinDocumentServices.read(testCheckinId)).thenReturn(Collections.emptySet());

        final HttpResponse<?> response = services.findFiles(testCheckinId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(Collections.emptySet(), response.getBody().get());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).read(any(UUID.class));
        verify(checkInServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindByCheckinIdForUnauthorizedUser() throws IOException {
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(testCheckinId));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(checkinDocumentServices, times(0)).read(any(UUID.class));
    }

    @Test
    void testFindFilesDriveCantConnect() throws IOException {
        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(null));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
    }

    @Test
    void testFindAllFilesThrowsGoogleJsonResponseException() throws IOException {

        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.execute()).thenThrow(testException);

        final HttpResponse<?> response = services.findFiles(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
    }

    @Test
    void testFindByCheckinIdThrowsGoogleJsonResponseException() throws IOException {

        UUID testCheckinId = UUID.randomUUID();
        final Set<CheckinDocument> testCheckinDocument = new HashSet<>();
        testCheckinDocument.add(testCd);
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(checkinDocumentServices.read(testCheckinId)).thenReturn(testCheckinDocument);
        when(testCd.getUploadDocId()).thenReturn("some.upload.doc.id");
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(any(String.class))).thenReturn(get);
        when(get.execute()).thenThrow(testException);

        final HttpResponse<?> response = services.findFiles(testCheckinId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).read(testCheckinId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testFindFilesThrowsIOException() throws IOException {
        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(googleDriveAccessor.accessGoogleDrive()).thenThrow(IOException.class);

        final HttpResponse<?> response = services.findFiles(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).read(any(UUID.class));
        verify(checkInServices, times(0)).read(any(UUID.class));
    }

    @Test
    void testDownloadFiles() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(testUploadDocId)).thenReturn(get);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws IOException {
                OutputStream outputStream = invocation.getArgument(0);
                InputStream inputstream = new FileInputStream(testFile);
                ByteStreams.copy(inputstream, outputStream);
                return null;
            }
        }).when(get).executeMediaAndDownloadTo(any(OutputStream.class));

        final HttpResponse<?> response = services.downloadFiles(testUploadDocId);
        java.io.File resultFile = (File) response.getBody().get();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testFile.length(), resultFile.length());
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkInServices, times(1)).read(any(UUID.class));
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(get, times(1)).executeMediaAndDownloadTo(any(OutputStream.class));
    }

    @Test
    void testDownloadFilesAdminCanAccess() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(testUploadDocId)).thenReturn(get);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws IOException {
                OutputStream outputStream = invocation.getArgument(0);
                InputStream inputstream = new FileInputStream(testFile);
                ByteStreams.copy(inputstream, outputStream);
                return null;
            }
        }).when(get).executeMediaAndDownloadTo(any(OutputStream.class));

        final HttpResponse<?> response = services.downloadFiles(testUploadDocId);
        java.io.File resultFile = (File) response.getBody().get();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testFile.length(), resultFile.length());
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkInServices, times(1)).read(any(UUID.class));
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(get, times(1)).executeMediaAndDownloadTo(any(OutputStream.class));
    }

    @Test
    void testDownloadFilesInvalidUploadDocId() throws IOException {
        String invalidUploadDocId = "some.test.id";

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(invalidUploadDocId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.downloadFiles(invalidUploadDocId));

        assertEquals(String.format("Unable to find record with id %s", invalidUploadDocId), responseException.getMessage());
        verify(googleDriveAccessor, times(0)).accessGoogleDrive();
        verify(checkInServices, times(0)).read(any(UUID.class));
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(invalidUploadDocId);
    }

    @Test
    void testDownloadFilesUnauthorizedUser() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.downloadFiles(testUploadDocId));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(googleDriveAccessor, times(0)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDownloadFilesDriveCantConnect() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testMemberProfileId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(checkInServices.read(any())).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);

        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.downloadFiles(testUploadDocId));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
    }

    @Test
    void testDownloadFilesThrowsGoogleJsonResponseException() throws IOException {

        String testUploadDocId = "some.test.id";
        UUID testMemberProfileId = UUID.randomUUID();
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(checkInServices.read(any())).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(testUploadDocId)).thenThrow(testException);

        final HttpResponse<?> response = services.downloadFiles(testUploadDocId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(checkInServices, times(1)).read(any());
    }

    @Test
    void testDownloadFileThrowsIOException() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(googleDriveAccessor.accessGoogleDrive()).thenThrow(IOException.class);

        final HttpResponse<?> response = services.downloadFiles(testUploadDocId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDeleteFile() throws IOException {

        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.delete(uploadDocId)).thenReturn(delete);

        final HttpResponse<?> response = services.deleteFile(uploadDocId);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkinDocumentServices, times(1)).deleteByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDeleteFilesAdminCanAccess() throws IOException {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.delete(uploadDocId)).thenReturn(delete);

        final HttpResponse<?> response = services.deleteFile(uploadDocId);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkinDocumentServices, times(1)).deleteByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDeleteFileWhenCheckinDocDoesntExist() throws IOException {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals(String.format("Unable to find record with id %s", uploadDocId), responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(0)).read(testCheckinId);
        verify(googleDriveAccessor, times(0)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileByUnauthorizedUser() throws IOException {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.delete(uploadDocId)).thenReturn(delete);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(googleDriveAccessor, times(0)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileDriveCantConnect() throws IOException {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        setupMocksForAuth();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileThrowsGoogleJsonResponseException() throws IOException {

        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.delete(uploadDocId)).thenReturn(delete);
        when(delete.execute()).thenThrow(testException);

        final HttpResponse<?> response = services.deleteFile(uploadDocId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileThrowsIOException() throws IOException {

        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(googleDriveAccessor.accessGoogleDrive()).thenThrow(IOException.class);

        final HttpResponse<?> response = services.deleteFile(uploadDocId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testUploadFilesByCreatingNewDirectory() throws IOException {

        //arrange
        String memberName = "testName";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        FileList fileList = new FileList();
        List<com.google.api.services.drive.model.File> mockFiles = new ArrayList<>();
        com.google.api.services.drive.model.File fileInService = new com.google.api.services.drive.model.File();
        fileInService.setId("some.id");
        fileInService.setName("some.file.name");
        mockFiles.add(fileInService);
        fileList.setFiles(mockFiles);

        com.google.api.services.drive.model.File newFolderCreatedOnDrive = new com.google.api.services.drive.model.File();
        newFolderCreatedOnDrive.setName("new.directory.name");

        com.google.api.services.drive.model.File fileFromDrive = new com.google.api.services.drive.model.File();
        fileFromDrive.setName("testFile");

        Drive.Files.Create createForFileUpload = mock(Drive.Files.Create.class);

        setupMocksForAuth();
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testCheckIn.isCompleted()).thenReturn(false);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(testMemberProfile.getName()).thenReturn(memberName);
        when(testMemberProfile.getId()).thenReturn(testMemberId);

        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.execute()).thenReturn(fileList);

        when(files.create(any(com.google.api.services.drive.model.File.class))).thenReturn(create);
        when(create.execute()).thenReturn(newFolderCreatedOnDrive);

        when(files.create(any(com.google.api.services.drive.model.File.class), any(AbstractInputStreamContent.class))).thenReturn(createForFileUpload);
        when(createForFileUpload.setSupportsAllDrives(true)).thenReturn(createForFileUpload);
        when(createForFileUpload.setFields(any(String.class))).thenReturn(createForFileUpload);
        when(createForFileUpload.execute()).thenReturn(fileFromDrive);

        //act
        final HttpResponse<?> response = services.uploadFile(testCheckinId, fileToUpload);
        FileInfoDTO result = (FileInfoDTO) response.getBody().get();

        //assert
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(fileToUpload.getFilename(), result.getName());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testUploadFilesToExistingDirectory() throws IOException {
        //arrange
        String memberName = "testName";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        FileList fileList = new FileList();
        List<com.google.api.services.drive.model.File> mockFiles = new ArrayList<>();
        com.google.api.services.drive.model.File fileInService = new com.google.api.services.drive.model.File();
        fileInService.setId("some.id");
        fileInService.setName(memberName.concat(LocalDate.now().toString()));
        mockFiles.add(fileInService);
        fileList.setFiles(mockFiles);

        com.google.api.services.drive.model.File fileFromDrive = new com.google.api.services.drive.model.File();
        fileFromDrive.setName("testFile");

        setupMocksForAuth();
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testCheckIn.isCompleted()).thenReturn(false);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(testMemberProfile.getName()).thenReturn(memberName);
        when(testMemberProfile.getId()).thenReturn(testMemberId);

        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(files.create(any(com.google.api.services.drive.model.File.class), any(AbstractInputStreamContent.class))).thenReturn(create);
        when(files.create(any(com.google.api.services.drive.model.File.class))).thenReturn(create);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.execute()).thenReturn(fileList);
        when(create.setSupportsAllDrives(true)).thenReturn(create);
        when(create.setFields(any(String.class))).thenReturn(create);
        when(create.execute()).thenReturn(fileFromDrive);

        //act
        final HttpResponse<?> response = services.uploadFile(testCheckinId, fileToUpload);
        FileInfoDTO result = (FileInfoDTO) response.getBody().get();

        //assert
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(fileToUpload.getFilename(), result.getName());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testFileUploadForAdminByUploadingFileToExistingDirectory() throws IOException {
        //arrange
        String memberName = "testName";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        FileList fileList = new FileList();
        List<com.google.api.services.drive.model.File> mockFiles = new ArrayList<>();
        com.google.api.services.drive.model.File fileInService = new com.google.api.services.drive.model.File();
        fileInService.setId("some.id");
        fileInService.setName(memberName.concat(LocalDate.now().toString()));
        mockFiles.add(fileInService);
        fileList.setFiles(mockFiles);

        com.google.api.services.drive.model.File fileFromDrive = new com.google.api.services.drive.model.File();
        fileFromDrive.setName("testFile");

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(testMemberProfile.getName()).thenReturn(memberName);

        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(files.create(any(com.google.api.services.drive.model.File.class), any(AbstractInputStreamContent.class))).thenReturn(create);
        when(files.create(any(com.google.api.services.drive.model.File.class))).thenReturn(create);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.execute()).thenReturn(fileList);
        when(create.setSupportsAllDrives(true)).thenReturn(create);
        when(create.setFields(any(String.class))).thenReturn(create);
        when(create.execute()).thenReturn(fileFromDrive);

        //act
        final HttpResponse<?> response = services.uploadFile(testCheckinId, fileToUpload);
        FileInfoDTO result = (FileInfoDTO) response.getBody().get();

        //assert
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(fileToUpload.getFilename(), result.getName());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testUploadFileThrowsErrorWhenFileNameIsEmpty() {
        setupMocksForAuth();
        when(fileToUpload.getFilename()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(UUID.randomUUID(), fileToUpload));

        assertEquals("Please select a valid file before uploading.", responseException.getMessage());
    }

    @Test
    void testUploadFileThrowsErrorForInvalidCheckinId() {
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(fileToUpload.getFilename()).thenReturn("test.file.name");
        when(checkInServices.read(testCheckinId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals(String.format("Unable to find checkin record with id %s", testCheckinId), responseException.getMessage());
    }

    @Test
    void testUploadFileUnauthorizedUser() throws IOException {
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(0)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(0)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileThrowsErrorIfCheckinIsComplete() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        setupMocksForAuth();
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testCheckIn.isCompleted()).thenReturn(true);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(0)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(0)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileDriveCantConnect() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(memberProfileServices.getById(testMemberId)).thenReturn(testMemberProfile);
        when(testMemberProfile.getName()).thenReturn("test.name");
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileThrowsIOException() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(memberProfileServices.getById(testMemberId)).thenReturn(testMemberProfile);
        when(testMemberProfile.getName()).thenReturn("test.name");
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(anyString())).thenReturn(list);
        when(list.execute()).thenThrow(IOException.class);

        final HttpResponse<?> response = services.uploadFile(testCheckinId, fileToUpload);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileThrowsGoogleJsonResponseException() throws IOException {
        //arrange
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);
        String memberName = "testName";
        UUID testCheckinId = UUID.randomUUID();

        setupMocksForAuth();
        when(securityService.hasRole(RoleType.Constants.ADMIN_ROLE)).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(testMemberProfile.getName()).thenReturn(memberName);

        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.execute()).thenThrow(testException);

        //act
        final HttpResponse<?> response = services.uploadFile(testCheckinId, fileToUpload);

        //assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        verify(googleDriveAccessor, times(1)).accessGoogleDrive();
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any());
        verify(checkinDocumentServices, times(0)).save(any());
    }
}