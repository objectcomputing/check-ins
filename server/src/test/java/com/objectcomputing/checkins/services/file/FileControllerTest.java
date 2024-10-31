package com.objectcomputing.checkins.services.file;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
//     => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.Mockito
@DisabledInNativeImage
class FileControllerTest extends TestContainersSuite {

    @Inject
    @Client("/services/files")
    private HttpClient client;

    private static File testFile;
    private final static String filePath = "testFile.txt";

    @Inject
    private FileServices fileServices;

    @BeforeAll
    void createTestFile() throws IOException {
        testFile = new File(filePath);
        FileWriter myWriter = new FileWriter(testFile);
        myWriter.write("This.Is.A.Test.File");
        myWriter.close();
    }

    @AfterAll
    void deleteTestFile() {
        testFile.deleteOnExit();
    }

    @Test
    void testFindAll() {

        UUID testCheckinId = UUID.randomUUID();

        FileInfoDTO testFileInfoDto = new FileInfoDTO();
        testFileInfoDto.setFileId("some.id");
        testFileInfoDto.setName(testFile.getName());
        testFileInfoDto.setCheckInId(testCheckinId);
        testFileInfoDto.setSize(testFile.length());

        Set<FileInfoDTO> testResultFromService = new HashSet<>();
        testResultFromService.add(testFileInfoDto);

        when(fileServices.findFiles(null)).thenReturn(testResultFromService);

        final HttpRequest<?> request = HttpRequest.GET("").basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));

        assertNotNull(response);
        Set<FileInfoDTO> result = response.getBody().get();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testFileInfoDto.getSize(), result.iterator().next().getSize());
        assertEquals(testFileInfoDto.getFileId(), result.iterator().next().getFileId());
        assertEquals(testFileInfoDto.getCheckInId(), result.iterator().next().getCheckInId());
        assertEquals(testFileInfoDto.getName(), result.iterator().next().getName());
        verify(fileServices, times(1)).findFiles(null);
    }

    @Test
    void testFindByCheckinId() {

        UUID testCheckinId = UUID.randomUUID();

        FileInfoDTO testFileInfoDto = new FileInfoDTO();
        testFileInfoDto.setFileId("some.id");
        testFileInfoDto.setName(testFile.getName());
        testFileInfoDto.setCheckInId(testCheckinId);
        testFileInfoDto.setSize(testFile.length());

        Set<FileInfoDTO> testResultFromService = new HashSet<>();
        testResultFromService.add(testFileInfoDto);

        when(fileServices.findFiles(testCheckinId)).thenReturn(testResultFromService);

        final HttpRequest<?> request = HttpRequest.GET(String.format("?id=%s", testCheckinId)).basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        Set<FileInfoDTO> result = response.getBody().get();
        assertEquals(testFileInfoDto.getSize(), result.iterator().next().getSize());
        assertEquals(testFileInfoDto.getFileId(), result.iterator().next().getFileId());
        assertEquals(testFileInfoDto.getCheckInId(), result.iterator().next().getCheckInId());
        assertEquals(testFileInfoDto.getName(), result.iterator().next().getName());
        verify(fileServices, times(1)).findFiles(testCheckinId);
    }

    @Test
    void testDownloadDocument() {
        String uploadDocId = "some.upload.id";

        when(fileServices.downloadFiles(uploadDocId)).thenReturn(testFile);

        final HttpRequest<?> request= HttpRequest.GET(String.format("/%s/download", uploadDocId))
                                                            .basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(fileServices, times(1)).downloadFiles(uploadDocId);
    }

    @Test
    void testUploadEndpoint() {

        UUID testCheckinId = UUID.randomUUID();

        FileInfoDTO testFileInfoDto = new FileInfoDTO();
        testFileInfoDto.setFileId("some.id");
        testFileInfoDto.setName(testFile.getName());
        testFileInfoDto.setCheckInId(testCheckinId);
        testFileInfoDto.setSize(testFile.length());

        when(fileServices.uploadFile(any(UUID.class), any(CompletedFileUpload.class))).thenReturn(testFileInfoDto);

        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", testCheckinId), MultipartBody.builder()
                                        .addPart("file", testFile).build())
                                        .basicAuth("some.email.id", MEMBER_ROLE)
                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> response = client.toBlocking().exchange(request, FileInfoDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        FileInfoDTO result = response.getBody().get();
        assertEquals(testFileInfoDto.getSize(), result.getSize());
        assertEquals(testFileInfoDto.getFileId(), result.getFileId());
        assertEquals(testFileInfoDto.getCheckInId(), result.getCheckInId());
        assertEquals(testFileInfoDto.getName(), result.getName());
        verify(fileServices, times(1)).uploadFile(any(UUID.class), any(CompletedFileUpload.class));
    }

    @Test
    void testUploadEndpointFailsForInvalidFile() {
        UUID testCheckinId = UUID.randomUUID();
        File badFile = new File("");

        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", testCheckinId), MultipartBody.builder()
                .addPart("file", badFile).build())
                .basicAuth("some.email", MEMBER_ROLE)
                .contentType(MULTIPART_FORM_DATA);

        final IllegalArgumentException responseException = assertThrows(IllegalArgumentException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        // Note: exception message is different here depending on your operating system. Better to just check the type.
        assertTrue(responseException.getMessage().contains("java.io.FileNotFoundException"));
        verify(fileServices, times(0)).uploadFile(any(UUID.class), any(CompletedFileUpload.class));
    }

    @Test
    void testDeleteEndpoint() {

        String uploadDocId = "some.upload.id";
        when(fileServices.deleteFile(uploadDocId)).thenReturn(true);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", uploadDocId))
                                        .basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(fileServices, times(1)).deleteFile(uploadDocId);
    }

    @Test
    void testHandleBadArgs() {

        String uploadDocId = "some.upload.id";
        doThrow(FileRetrievalException.class).when(fileServices).deleteFile(uploadDocId);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", uploadDocId))
                .basicAuth("some.email.id", MEMBER_ROLE);

        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        verify(fileServices, times(1)).deleteFile(uploadDocId);
    }

    @MockBean(FileServices.class)
    public FileServices fileServices() {
        return mock(FileServices.class);
    }
}
