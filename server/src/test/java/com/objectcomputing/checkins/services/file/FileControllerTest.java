package com.objectcomputing.checkins.services.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.CheckInDocumentFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.FileWriter;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, CheckInDocumentFixture {

    @Inject
    @Client("/services/file")
    private HttpClient client;

    private static File testFile;
    private final static String filePath = "testFile.txt";

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
    public void testUploadEndpoint() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        // test folder creation and file upload
        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder()
                                        .addPart("file", testFile).build())
                                        .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> response = client.toBlocking().exchange(request, FileInfoDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testFile.getName(), response.getBody().get().getName());
        assertEquals(checkin.getId(), response.getBody().get().getCheckInId());
        assertTrue(response.getBody().get().getSize() > 0);
        assertNotNull(response.getBody().get().getFileId());

        // test usage of existing upload directory to upload file
        final HttpRequest<?> requestExistingFolder = HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder()
                                                        .addPart("file", testFile).build())
                                                        .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> responseExistingFolder = client.toBlocking().exchange(requestExistingFolder, FileInfoDTO.class);

        assertEquals(HttpStatus.OK, responseExistingFolder.getStatus());
        assertEquals(testFile.getName(), responseExistingFolder.getBody().get().getName());
        assertEquals(checkin.getId(), responseExistingFolder.getBody().get().getCheckInId());
        assertTrue(responseExistingFolder.getBody().get().getSize() > 0);
        assertNotNull(responseExistingFolder.getBody().get().getFileId());
    }

    @Test
    public void testUploadEndpointFailsForInvalidFile() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        File badFile = new File("");

        final HttpRequest<?> request = HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder()
                .addPart("file", badFile).build())
                .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                .contentType(MULTIPART_FORM_DATA);

        final IllegalArgumentException responseException = assertThrows(IllegalArgumentException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        String error = responseException.getMessage();
        assertEquals("java.io.FileNotFoundException:  (No such file or directory)", error);
    }

    @Test
    public void testDownloadEndpoint() throws IOException {

        //arrange
        String expected = Files.readString(Path.of(filePath));
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> requestForPostEndpoint = HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder()
                                                        .addPart("file", testFile).build())
                                                        .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> responseFromPostEndpoint = client.toBlocking().exchange(requestForPostEndpoint, FileInfoDTO.class);
        assertEquals(HttpStatus.OK, responseFromPostEndpoint.getStatus());

        //act
        final HttpRequest<?> requestForDownloadEndpoint= HttpRequest.GET(String.format("/%s/download",
                                                            responseFromPostEndpoint.getBody().get().getFileId()))
                                                            .basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<File> responseFromDownloadEndpoint = client.toBlocking().exchange(requestForDownloadEndpoint, File.class);
        String actual = responseFromDownloadEndpoint.getBody().get().toString();

        //assert
        assertEquals(HttpStatus.OK, responseFromDownloadEndpoint.getStatus());
        assertEquals(expected, actual);
    }

    @Test
    public void testDownloadEndpointThrowsExceptionIfDocIdDoesNotExist() {
        MemberProfile user = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET("/test.file.id/download")
                .basicAuth(user.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND,  responseException.getStatus());
    }

    @Test
    public void testFindAllEndpointAdmin() {
        MemberProfile user = createAnUnrelatedUser();
        final HttpRequest<?> request = HttpRequest.GET("").basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));
        assertEquals(HttpStatus.OK,response.getStatus());
        assertTrue(response.getBody().isPresent());
    }

    @Test
    public void testFindAllFailsIfNotAdmin() {

        MemberProfile user = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET("").basicAuth(user.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(HttpStatus.BAD_REQUEST,  responseException.getStatus());
        assertEquals("You are not authorized to perform this operation", error);
    }

    @Test
    public void testFindByCheckInId() {

        //arrange
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> requestForPostEndpoint = HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder()
                                                        .addPart("file", testFile).build())
                                                        .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> responseFromPostEndpoint = client.toBlocking().exchange(requestForPostEndpoint, FileInfoDTO.class);
        assertEquals(HttpStatus.OK, responseFromPostEndpoint.getStatus());
        FileInfoDTO resultFromPost = responseFromPostEndpoint.getBody().get();

        //act
        final HttpRequest<?> requestForFindById = HttpRequest.GET(String.format("?id=%s", checkin.getId()))
                                                    .basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<FileInfoDTO>> responseFromFindById = client.toBlocking().exchange(requestForFindById, Argument.setOf(FileInfoDTO.class));
        FileInfoDTO result = Objects.requireNonNull(responseFromFindById.body()).iterator().next();

        //assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, responseFromFindById.getStatus());
        assertEquals(resultFromPost.getFileId(), result.getFileId());
        assertEquals(resultFromPost.getCheckInId(), result.getCheckInId());
        assertEquals(resultFromPost.getName(), result.getName());
    }

    @Test
    public void testFindByCheckInIdFailsForUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("?id=%s", checkin.getId()))
                                        .basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("Internal Server Error: You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,  responseException.getStatus());
    }

    @Test
    public void testFindByCheckInIdThrowsExceptionIfDocDoesNotExist() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        createADefaultCheckInDocument(checkin);

        final HttpRequest<?> request= HttpRequest.GET(String.format("?id=%s", checkin.getId()))
                                        .basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("File not found: doc1.", error);
        assertEquals(HttpStatus.NOT_FOUND,  responseException.getStatus());
    }

    @Test
    public void testDeleteEndpoint() {
        //arrange
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> requestForPostEndpoint = HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder()
                .addPart("file", testFile).build())
                .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> responseFromPostEndpoint = client.toBlocking().exchange(requestForPostEndpoint, FileInfoDTO.class);
        assertEquals(HttpStatus.OK, responseFromPostEndpoint.getStatus());
        FileInfoDTO resultFromPost = responseFromPostEndpoint.getBody().get();

        //act
        final HttpRequest<?> requestForDelete = HttpRequest.DELETE(String.format("/%s", resultFromPost.getFileId()))
                .basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<FileInfoDTO>> responseFromDelete = client.toBlocking().exchange(requestForDelete);

        //assert
        assertEquals(HttpStatus.OK,  responseFromDelete.getStatus());
        assertEquals(Optional.empty(), responseFromDelete.getBody());
    }

    @Test
    public void testDeleteEndpointThrowsExceptionIfDocDoesNotExist() {
        //arrange
        MemberProfile user = createAnUnrelatedUser();

        //act
        final HttpRequest<?> request = HttpRequest.DELETE("/test.file.id")
                .basicAuth(user.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        //assert
        assertEquals("File not found: test.file.id.", error);
        assertEquals(HttpStatus.NOT_FOUND,  responseException.getStatus());
    }
}
