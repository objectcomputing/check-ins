package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.testing.json.GoogleJsonResponseExceptionFactoryTesting;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.common.io.ByteStreams;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.authentication.Authentication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class FileServicesImplTest extends TestContainersSuite {

    private static File testFile;
    private final static String filePath = "testFile.txt";
    final static JsonFactory jsonFactory = new MockJsonFactory();

    @Mock
    private Authentication authentication;

    @Mock
    private Map mockAttributes;

    @Mock
    private Drive drive;

    @Mock
    private Drive.Files files;

    @Mock
    private Drive.Files.List list;

    @Mock
    private Drive.Files.Get get;

    @Mock
    private Drive.Files.Delete delete;

    @Mock
    private Drive.Files.Create create;

    @Mock
    private Drive.Files.Update update;

    @Mock
    private CompletedFileUpload fileToUpload;

    @Mock
    private MemberProfile testMemberProfile;

    @Mock
    private CheckIn testCheckIn;

    @Mock
    private CheckinDocument testCd;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private CheckInServices checkInServices;

    @Mock
    private CheckinDocumentServices checkinDocumentServices;

    @Mock
    private GoogleApiAccess mockGoogleApiAccess;

    @Mock
    private CurrentUserServices currentUserServices;

    @Mock
    private MemberProfileServices memberProfileServices;

    @Mock
    private CompletedFileUpload completedFileUpload;

    @Mock
    private GoogleServiceConfiguration googleServiceConfiguration;

    @InjectMocks
    private FileServicesImpl services;

    private AutoCloseable mockFinalizer;

    @BeforeAll
    void initMocksAndInitializeFile() throws IOException {
        mockFinalizer = openMocks(this);
        testFile = new File(filePath);
        FileWriter myWriter = new FileWriter(testFile);
        myWriter.write("This.Is.A.Test.File");
        myWriter.close();
    }

    @AfterAll
    void deleteTestFile() throws Exception {
        testFile.deleteOnExit();
        mockFinalizer.close();
    }

    @BeforeEach
    void resetMocks() {
        reset(authentication);
        reset(mockAttributes);
        reset(drive);
        reset(files);
        reset(list);
        reset(get);
        reset(delete);
        reset(create);
        reset(update);
        reset(fileToUpload);
        reset(testMemberProfile);
        reset(testCheckIn);
        reset(testCd);
        reset(mockInputStream);
        reset(checkInServices);
        reset(checkinDocumentServices);
        reset(mockGoogleApiAccess);
        reset(currentUserServices);
        reset(memberProfileServices);
        reset(completedFileUpload);
        reset(googleServiceConfiguration);

        when(authentication.getAttributes()).thenReturn(mockAttributes);
        when(mockAttributes.get("email")).thenReturn(mockAttributes);
        when(mockAttributes.toString()).thenReturn("test.email");
        when(currentUserServices.findOrSaveUser(any(), any(), any())).thenReturn(testMemberProfile);
    }

    @Test
    void testFindFilesForFindAll() throws IOException {

        FileList fileList = new FileList();
        List<com.google.api.services.drive.model.File> mockFiles = new ArrayList<>();
        com.google.api.services.drive.model.File file1 = new com.google.api.services.drive.model.File();
        file1.setId("some.id");
        mockFiles.add(file1);
        fileList.setFiles(mockFiles);

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenReturn(fileList);

        final Set<FileInfoDTO> result = services.findFiles(null);

        assertNotNull(result);
        assertEquals(fileList.getFiles().iterator().next().getId(), result.iterator().next().getFileId());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(0)).read(any(UUID.class));
        verify(checkInServices, times(0)).read(any(UUID.class));
    }

    @Test
    void testFindAllFailsIfNotAdmin() {
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(null));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testFindFilesForFindByCheckinId() throws IOException {

        com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
        file.setId("some.id");

        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberProfileId = UUID.randomUUID();
        final Set<CheckinDocument> testCheckinDocument = new HashSet<>();
        testCheckinDocument.add(testCd);

        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(any(String.class))).thenReturn(get);
        when(get.setSupportsAllDrives(any())).thenReturn(get);
        when(get.execute()).thenReturn(file);
        when(checkInServices.accessGranted(testCheckinId, testMemberProfileId)).thenReturn(true);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(checkinDocumentServices.read(testCheckinId)).thenReturn(testCheckinDocument);
        when(testCd.getUploadDocId()).thenReturn("some.upload.doc.id");

        final Set<FileInfoDTO> result = services.findFiles(testCheckinId);

        assertNotNull(result);
        assertEquals(file.getId(), result.iterator().next().getFileId());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).read(testCheckinId);
        verify(checkInServices, times(1)).accessGranted(testCheckinId, testMemberProfileId);
    }

    @Test
    void testFindByCheckinIdWhenNoDocsAreUploadedForCheckinId() {

        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberProfileId = UUID.randomUUID();
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(checkInServices.accessGranted(testCheckinId, testMemberProfileId)).thenReturn(true);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(checkinDocumentServices.read(testCheckinId)).thenReturn(Collections.emptySet());

        final Set<FileInfoDTO> result = services.findFiles(testCheckinId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).read(any(UUID.class));
        verify(checkInServices, times(1)).accessGranted(testCheckinId, testMemberProfileId);
    }

    @Test
    void testFindByCheckinIdForUnauthorizedUser() {
        UUID testCheckinId = UUID.randomUUID();
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(checkInServices.accessGranted(testCheckinId, testMemberProfile.getId())).thenReturn(false);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(testCheckinId));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkInServices, times(1)).accessGranted(testCheckinId, testMemberProfile.getId());
        verify(checkinDocumentServices, times(0)).read(any(UUID.class));
    }

    @Test
    void testFindFilesDriveCantConnect() {
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(mockGoogleApiAccess.getDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.findFiles(null));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
    }

    @Test
    void testFindAllFilesThrowsGoogleJsonResponseException() throws IOException {

        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenThrow(testException);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                () -> services.findFiles(null));

        assertEquals(testException.getMessage(), responseException.getMessage());
        verify(mockGoogleApiAccess, times(1)).getDrive();
    }

    @Test
    void testFindByCheckinIdThrowsGoogleJsonResponseException() throws IOException {

        UUID testCheckinId = UUID.randomUUID();
        final Set<CheckinDocument> testCheckinDocument = new HashSet<>();
        testCheckinDocument.add(testCd);
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(checkInServices.accessGranted(testCheckinId, testMemberProfile.getId())).thenReturn(true);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(checkinDocumentServices.read(testCheckinId)).thenReturn(testCheckinDocument);
        when(testCd.getUploadDocId()).thenReturn("some.upload.doc.id");
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(any(String.class))).thenReturn(get);
        when(get.setSupportsAllDrives(any())).thenReturn(get);
        when(get.execute()).thenThrow(testException);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                                                            () -> services.findFiles(testCheckinId));

        assertEquals(testException.getMessage(), responseException.getMessage());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).read(testCheckinId);
        verify(checkInServices, times(1)).accessGranted(testCheckinId, testMemberProfile.getId());
    }

    @Test
    void testDownloadFiles() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(testUploadDocId)).thenReturn(get);
        when(get.setSupportsAllDrives(any())).thenReturn(get);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws IOException {
                OutputStream outputStream = invocation.getArgument(0);
                InputStream inputstream = new FileInputStream(testFile);
                ByteStreams.copy(inputstream, outputStream);
                return null;
            }
        }).when(get).executeMediaAndDownloadTo(any(OutputStream.class));

        final java.io.File resultFile = services.downloadFiles(testUploadDocId);

        assertEquals(testFile.length(), resultFile.length());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkInServices, times(1)).read(any(UUID.class));
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(get, times(1)).executeMediaAndDownloadTo(any(OutputStream.class));
    }

    @Test
    void testDownloadFilesAdminCanAccess() throws IOException {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();

        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(testUploadDocId)).thenReturn(get);
        when(get.setSupportsAllDrives(any())).thenReturn(get);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws IOException {
                OutputStream outputStream = invocation.getArgument(0);
                InputStream inputstream = new FileInputStream(testFile);
                ByteStreams.copy(inputstream, outputStream);
                return null;
            }
        }).when(get).executeMediaAndDownloadTo(any(OutputStream.class));

        final java.io.File resultFile = services.downloadFiles(testUploadDocId);

        assertEquals(testFile.length(), resultFile.length());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkInServices, times(1)).read(any(UUID.class));
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(get, times(1)).executeMediaAndDownloadTo(any(OutputStream.class));
    }

    @Test
    void testDownloadFilesInvalidUploadDocId() {
        String invalidUploadDocId = "some.test.id";
        when(checkinDocumentServices.getFindByUploadDocId(invalidUploadDocId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.downloadFiles(invalidUploadDocId));

        assertEquals(String.format("Unable to find record with id %s", invalidUploadDocId), responseException.getMessage());
        verify(mockGoogleApiAccess, times(0)).getDrive();
        verify(checkInServices, times(0)).read(any(UUID.class));
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(invalidUploadDocId);
    }

    @Test
    void testDownloadFilesUnauthorizedUser() {
        String testUploadDocId = "some.test.id";
        UUID testCheckinId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.downloadFiles(testUploadDocId));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
        verify(mockGoogleApiAccess, times(0)).getDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDownloadFilesDriveCantConnect() {
        String testUploadDocId = "some.test.id";
        UUID testMemberProfileId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(checkInServices.read(any())).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(mockGoogleApiAccess.getDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.downloadFiles(testUploadDocId));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
        verify(mockGoogleApiAccess, times(1)).getDrive();
    }

    @Test
    void testDownloadFilesThrowsGoogleJsonResponseException() throws IOException {

        String testUploadDocId = "some.test.id";
        UUID testMemberProfileId = UUID.randomUUID();
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(checkinDocumentServices.getFindByUploadDocId(testUploadDocId)).thenReturn(testCd);
        when(checkInServices.read(any())).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberProfileId);
        when(testMemberProfile.getId()).thenReturn(testMemberProfileId);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.get(testUploadDocId)).thenThrow(testException);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                                                            () -> services.downloadFiles(testUploadDocId));

        assertEquals(testException.getMessage(), responseException.getMessage());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(testUploadDocId);
        verify(checkInServices, times(1)).read(any());
    }

    @Test
    void testDeleteFile() throws IOException {

        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.update(eq(uploadDocId), any())).thenReturn(update);
        when(update.setSupportsAllDrives(any())).thenReturn(update);

        Boolean result = services.deleteFile(uploadDocId);

        assertTrue(result);
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkinDocumentServices, times(1)).deleteByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDeleteFilesAdminCanAccess() throws IOException {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.update(eq(uploadDocId), any())).thenReturn(update);
        when(update.setSupportsAllDrives(any())).thenReturn(update);

        Boolean result = services.deleteFile(uploadDocId);

        assertTrue(result);
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkinDocumentServices, times(1)).deleteByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
    }

    @Test
    void testDeleteFileWhenCheckinDocDoesntExist() {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals(String.format("Unable to find record with id %s", uploadDocId), responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(0)).read(testCheckinId);
        verify(mockGoogleApiAccess, times(0)).getDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileByUnauthorizedUser() throws IOException {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.delete(uploadDocId)).thenReturn(delete);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(mockGoogleApiAccess, times(0)).getDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileDriveCantConnect() {
        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(mockGoogleApiAccess.getDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.deleteFile(uploadDocId));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(0)).deleteByUploadDocId(uploadDocId);
    }

    @Test
    void testDeleteFileThrowsGoogleJsonResponseException() throws IOException {

        String uploadDocId = "Some.Upload.Doc.Id";
        UUID testCheckinId = UUID.randomUUID();
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(checkinDocumentServices.getFindByUploadDocId(uploadDocId)).thenReturn(testCd);
        when(testCd.getCheckinsId()).thenReturn(testCheckinId);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.update(eq(uploadDocId), any())).thenReturn(update);
        when(update.setSupportsAllDrives(any())).thenReturn(update);
        when(update.execute()).thenThrow(testException);

        //act
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                () -> services.deleteFile(uploadDocId));

        //assert
        assertEquals(testException.getMessage(), responseException.getMessage());
        verify(checkinDocumentServices, times(1)).getFindByUploadDocId(uploadDocId);
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(mockGoogleApiAccess, times(1)).getDrive();
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

        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testCheckIn.isCompleted()).thenReturn(false);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(MemberProfileUtils.getFullName(testMemberProfile)).thenReturn(memberName);
        when(testMemberProfile.getId()).thenReturn(testMemberId);

        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenReturn(fileList);

        when(files.create(any(com.google.api.services.drive.model.File.class))).thenReturn(create);
        when(create.setSupportsAllDrives(any())).thenReturn(create);
        when(create.execute()).thenReturn(newFolderCreatedOnDrive);

        when(files.create(any(com.google.api.services.drive.model.File.class), any(AbstractInputStreamContent.class))).thenReturn(createForFileUpload);
        when(createForFileUpload.setSupportsAllDrives(true)).thenReturn(createForFileUpload);
        when(createForFileUpload.setFields(any(String.class))).thenReturn(createForFileUpload);
        when(createForFileUpload.execute()).thenReturn(fileFromDrive);

        //act
        FileInfoDTO result = services.uploadFile(testCheckinId, fileToUpload);

        //assert
        assertNotNull(result);
        assertEquals(fileToUpload.getFilename(), result.getName());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(1)).getDrive();
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

        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testCheckIn.isCompleted()).thenReturn(false);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(MemberProfileUtils.getFullName(testMemberProfile)).thenReturn(memberName);
        when(testMemberProfile.getId()).thenReturn(testMemberId);

        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(files.create(any(com.google.api.services.drive.model.File.class), any(AbstractInputStreamContent.class))).thenReturn(create);
        when(files.create(any(com.google.api.services.drive.model.File.class))).thenReturn(create);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenReturn(fileList);
        when(create.setSupportsAllDrives(true)).thenReturn(create);
        when(create.setFields(any(String.class))).thenReturn(create);
        when(create.execute()).thenReturn(fileFromDrive);

        //act
        FileInfoDTO result = services.uploadFile(testCheckinId, fileToUpload);

        //assert
        assertNotNull(result);
        assertEquals(fileToUpload.getFilename(), result.getName());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(1)).getDrive();
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

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(MemberProfileUtils.getFullName(testMemberProfile)).thenReturn(memberName);

        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(files.create(any(com.google.api.services.drive.model.File.class), any(AbstractInputStreamContent.class))).thenReturn(create);
        when(files.create(any(com.google.api.services.drive.model.File.class))).thenReturn(create);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenReturn(fileList);
        when(create.setSupportsAllDrives(true)).thenReturn(create);
        when(create.setFields(any(String.class))).thenReturn(create);
        when(create.execute()).thenReturn(fileFromDrive);

        //act
        FileInfoDTO result = services.uploadFile(testCheckinId, fileToUpload);

        //assert
        assertNotNull(result);
        assertEquals(fileToUpload.getFilename(), result.getName());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testUploadFileThrowsErrorWhenFileNameIsEmpty() {
        when(fileToUpload.getFilename()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(UUID.randomUUID(), fileToUpload));

        assertEquals("Please select a valid file before uploading.", responseException.getMessage());
    }

    @Test
    void testUploadFileThrowsErrorForInvalidCheckinId() {
        UUID testCheckinId = UUID.randomUUID();

        when(fileToUpload.getFilename()).thenReturn("test.file.name");
        when(checkInServices.read(testCheckinId)).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals(String.format("Unable to find checkin record with id %s", testCheckinId), responseException.getMessage());
    }

    @Test
    void testUploadFileUnauthorizedUser() throws IOException {
        UUID testCheckinId = UUID.randomUUID();

        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(UUID.randomUUID());

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(0)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(0)).getDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileThrowsErrorIfCheckinIsComplete() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(currentUserServices.getCurrentUser()).thenReturn(testMemberProfile);
        when(testMemberProfile.getId()).thenReturn(testMemberId);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(testCheckIn.isCompleted()).thenReturn(true);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(0)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(0)).getDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileDriveCantConnect() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);
        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(memberProfileServices.getById(testMemberId)).thenReturn(testMemberProfile);
        when(MemberProfileUtils.getFullName(testMemberProfile)).thenReturn("test.name");
        when(mockGoogleApiAccess.getDrive()).thenReturn(null);

        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class, () ->
                services.uploadFile(testCheckinId, fileToUpload));

        assertEquals("Unable to access Google Drive", responseException.getMessage());
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileThrowsIOException() throws IOException {
        UUID testCheckinId = UUID.randomUUID();
        UUID testMemberId = UUID.randomUUID();

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(testMemberId);
        when(memberProfileServices.getById(testMemberId)).thenReturn(testMemberProfile);
        when(MemberProfileUtils.getFullName(testMemberProfile)).thenReturn("test.name");
        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenThrow(IOException.class);

        assertThrows(FileRetrievalException.class, () -> services.uploadFile(testCheckinId, fileToUpload));
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any(UUID.class));
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkinDocumentServices, times(0)).save(any());
    }

    @Test
    void testUploadFileThrowsGoogleJsonResponseException() throws IOException {
        //arrange
        GoogleJsonResponseException testException = GoogleJsonResponseExceptionFactoryTesting.newMock(jsonFactory, 404, null);
        String memberName = "testName";
        UUID testCheckinId = UUID.randomUUID();

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(fileToUpload.getFilename()).thenReturn("testFile");
        when(fileToUpload.getInputStream()).thenReturn(mockInputStream);

        when(checkInServices.read(testCheckinId)).thenReturn(testCheckIn);
        when(testCheckIn.getTeamMemberId()).thenReturn(UUID.randomUUID());
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(testMemberProfile);
        when(MemberProfileUtils.getFullName(testMemberProfile)).thenReturn(memberName);

        when(mockGoogleApiAccess.getDrive()).thenReturn(drive);
        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(list);
        when(list.setFields(any(String.class))).thenReturn(list);
        when(list.setSupportsAllDrives(any())).thenReturn(list);
        when(list.setIncludeItemsFromAllDrives(any())).thenReturn(list);
        when(list.setQ(any())).thenReturn(list);
        when(list.setSpaces(any())).thenReturn(list);
        when(list.execute()).thenThrow(testException);

        //act
        final FileRetrievalException responseException = assertThrows(FileRetrievalException.class,
                () -> services.uploadFile(testCheckinId, fileToUpload));

        //assert
        assertEquals(testException.getMessage(), responseException.getMessage());
        verify(mockGoogleApiAccess, times(1)).getDrive();
        verify(checkInServices, times(1)).read(testCheckinId);
        verify(memberProfileServices, times(1)).getById(any());
        verify(checkinDocumentServices, times(0)).save(any());
    }
}
