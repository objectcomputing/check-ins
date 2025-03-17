package com.objectcomputing.checkins.services.file;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.FileServicesImplReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.CheckInDocumentFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;

import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.PDL_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.fileservicesimpl", value = StringUtils.TRUE)
class FileControllerTest extends TestContainersSuite
                         implements MemberProfileFixture, CheckInFixture, CheckInDocumentFixture, RoleFixture {

    @Inject
    @Client("/services/files")
    private HttpClient client;

    private static File testFile;
    private final static String filePath = "testFile.txt";

    @Inject
    private FileServicesImplReplacement fileServices;

    private CheckIn checkIn;
    private MemberProfile pdl;
    private MemberProfile member;

    @BeforeEach
    void reset() {
        createAndAssignRoles();
        pdl = createADefaultMemberProfile();
        member = createADefaultMemberProfileForPdl(pdl);
        checkIn = createADefaultCheckIn(member, pdl);
    }

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
        String fileId = "some.id";
        FileInfoDTO testFileInfoDto = fileServices.addFile(fileId, new byte[0]);



        final HttpRequest<?> request = HttpRequest.GET("").basicAuth(member.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));

        assertNotNull(response);
        Set<FileInfoDTO> result = response.getBody().get();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testFileInfoDto.getSize(), result.iterator().next().getSize());
        assertEquals(testFileInfoDto.getFileId(), result.iterator().next().getFileId());
        assertEquals(testFileInfoDto.getCheckInId(), result.iterator().next().getCheckInId());
        assertEquals(testFileInfoDto.getName(), result.iterator().next().getName());
    }

    private FileInfoDTO createUploadedDocument(String fileId) {
        return fileServices.addFile(fileId, new byte[50], (id) -> {
            return createACustomCheckInDocument(checkIn, id);
        });
    }

    @Test
    void testFindByCheckinId() {
        String fileId = "some.id";
        FileInfoDTO testFileInfoDto = createUploadedDocument(fileId);

        final HttpRequest<?> request = HttpRequest.GET(String.format("?id=%s", checkIn.getId())).basicAuth(pdl.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        Set<FileInfoDTO> result = response.getBody().get();
        assertEquals(testFileInfoDto.getSize(), result.iterator().next().getSize());
        assertEquals(testFileInfoDto.getFileId(), result.iterator().next().getFileId());
        assertEquals(testFileInfoDto.getCheckInId(), result.iterator().next().getCheckInId());
        assertEquals(testFileInfoDto.getName(), result.iterator().next().getName());
    }

    @Test
    void testDownloadDocument() {
        String uploadDocId = "some.upload.id";
        FileInfoDTO testFileInfoDto = createUploadedDocument(uploadDocId);

        final HttpRequest<?> request= HttpRequest.GET(String.format("/%s/download", uploadDocId))
                                                            .basicAuth(pdl.getWorkEmail(), PDL_ROLE);
        final HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUploadEndpoint() {
        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", checkIn.getId()), MultipartBody.builder()
                                        .addPart("file", testFile).build())
                                        .basicAuth(pdl.getWorkEmail(), PDL_ROLE)
                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> response = client.toBlocking().exchange(request, FileInfoDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        FileInfoDTO result = response.getBody().get();
        assertEquals(testFile.length(), result.getSize());
        assertEquals(checkIn.getId(), result.getCheckInId());
        assertEquals(testFile.getName(), result.getName());
    }

    @Test
    void testUploadEndpointFailsForInvalidFile() {
        File badFile = new File("");

        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", checkIn.getId()), MultipartBody.builder()
                .addPart("file", badFile).build())
                .basicAuth(member.getWorkEmail(), MEMBER_ROLE)
                .contentType(MULTIPART_FORM_DATA);

        final IllegalArgumentException responseException = assertThrows(IllegalArgumentException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        // Note: exception message is different here depending on your operating system. Better to just check the type.
        assertTrue(responseException.getMessage().contains("java.io.FileNotFoundException"));
    }

    @Test
    void testDeleteEndpoint() {
        String uploadDocId = "some.upload.id";
        FileInfoDTO testFileInfoDto = createUploadedDocument(uploadDocId);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", uploadDocId))
                                        .basicAuth(pdl.getWorkEmail(), PDL_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testHandleBadArgs() {
        String uploadDocId = "some.upload.id";

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", uploadDocId))
                .basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}
