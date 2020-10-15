package com.objectcomputing.checkins.services.file;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileControllerTest {

    @Inject
    @Client("/services/file")
    private HttpClient client;

    private static File testFile;
    private final static String filePath = "testFile.txt";
    private final static HttpResponse expectedResponse = mock(HttpResponse.class);

    @Inject
    private FileServices fileServices;

    @MockBean(FileServices.class)
    public FileServices fileServices() {
        return mock(FileServices.class);
    }

    @BeforeAll
    void createTestFile() throws IOException {
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

    @Test
    public void testFindAll() {

        when(fileServices.findFiles(null)).thenReturn(expectedResponse);

        final HttpRequest<?> request = HttpRequest.GET("").basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertNotNull(response);
        verify(fileServices, times(1)).findFiles(null);
    }

    @Test
    public void testFindByCheckinId() {

        UUID testCheckinId = UUID.randomUUID();
        when(fileServices.findFiles(testCheckinId)).thenReturn(expectedResponse);

        final HttpRequest<?> request = HttpRequest.GET(String.format("?id=%s", testCheckinId)).basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertNotNull(response);
        verify(fileServices, times(1)).findFiles(testCheckinId);
    }

    @Test
    public void testDownloadDocument() {

        String uploadDocId = "some.upload.id";
        when(fileServices.downloadFiles(uploadDocId)).thenReturn(expectedResponse);

        final HttpRequest<?> request= HttpRequest.GET(String.format("/%s/download", uploadDocId))
                                                            .basicAuth("some.email", MEMBER_ROLE);
        final HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertNotNull(response);
        assertEquals(MediaType.MULTIPART_FORM_DATA, response.getContentType().get().toString());
        verify(fileServices, times(1)).downloadFiles(uploadDocId);
    }

    @Test
    public void testUploadEndpoint() {

        UUID testCheckinId = UUID.randomUUID();
        when(fileServices.uploadFile(any(UUID.class), any(CompletedFileUpload.class))).thenReturn(expectedResponse);

        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", testCheckinId), MultipartBody.builder()
                                        .addPart("file", testFile).build())
                                        .basicAuth("some.email", MEMBER_ROLE)
                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> response = client.toBlocking().exchange(request, FileInfoDTO.class);

        assertNotNull(response);
        verify(fileServices, times(1)).uploadFile(any(UUID.class), any(CompletedFileUpload.class));
    }

    @Test
    public void testUploadEndpointFailsForInvalidFile() {

        UUID testCheckinId = UUID.randomUUID();
        File badFile = new File("");

        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", testCheckinId), MultipartBody.builder()
                .addPart("file", badFile).build())
                .basicAuth("some.email", MEMBER_ROLE)
                .contentType(MULTIPART_FORM_DATA);

        final IllegalArgumentException responseException = assertThrows(IllegalArgumentException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        String error = responseException.getMessage();
        assertEquals("java.io.FileNotFoundException:  (No such file or directory)", error);
        verify(fileServices, times(0)).uploadFile(any(UUID.class), any(CompletedFileUpload.class));
    }

    @Test
    public void testDeleteEndpoint() {

        String uploadDocId = "some.upload.id";
        when(fileServices.deleteFile(uploadDocId)).thenReturn(expectedResponse);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", uploadDocId))
                                        .basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertNotNull(response);
        verify(fileServices, times(1)).deleteFile(uploadDocId);
    }

    @Test
    public void testHandleBadArgs() {

        String uploadDocId = "some.upload.id";
        when(fileServices.deleteFile(uploadDocId)).thenThrow(FileRetrievalException.class);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", uploadDocId))
                .basicAuth("some.email.id", MEMBER_ROLE);

        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        verify(fileServices, times(1)).deleteFile(uploadDocId);
    }
}
