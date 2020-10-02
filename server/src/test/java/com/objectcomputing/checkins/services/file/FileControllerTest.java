package com.objectcomputing.checkins.services.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;

import javax.inject.Inject;
import java.io.*;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.*;

public class FileControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, CheckInDocumentFixture {

    @Inject
    @Client("/services/file")
    private HttpClient client;

    private static File testFile;

    @BeforeEach
    void createTestFile() throws IOException {
        testFile = new File("testFile.txt");
        FileWriter myWriter = new FileWriter("testFile.txt");
        myWriter.write("This.Is.A.Test.File");
        myWriter.close();
    }

    // Happy Path
    @Test
    public void testUploadEndpoint() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request= HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder().addPart("file", testFile).build())
                                        .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE)
                                        .contentType(MULTIPART_FORM_DATA);
        final HttpResponse<FileInfoDTO> response = client.toBlocking().exchange(request, FileInfoDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testFile.getName(), response.getBody().get().getName());
        assertEquals(checkin.getId(), response.getBody().get().getCheckInId());
        assertTrue(response.getBody().get().getSize() > 0);
        assertNotNull(response.getBody().get().getFileId());
    }

    @Test
    public void testUploadEndpointFailsForUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request= HttpRequest.POST(String.format("/%s", checkin.getId()), MultipartBody.builder().addPart("file", testFile).build())
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), MEMBER_ROLE)
                .contentType(MULTIPART_FORM_DATA);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("Internal Server Error: You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.UNAUTHORIZED,  responseException.getStatus());
    }

    @Test
    public void testDownloadEndpoint() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        String id = "1Ji7hrjp4c2-vBPYm6kyjkAyXI724Bvgp";
        final HttpRequest<?> request= HttpRequest.GET(String.format("/%s/download", id))
                .basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request);
    }

    @Test
    public void testFindAllEndpointAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        String id = "1Ji7hrjp4c2-vBPYm6kyjkAyXI724Bvgp";
        final HttpRequest<?> request= HttpRequest.GET("")
                .basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));
        assertEquals(HttpStatus.OK,response.getStatus());
        assertTrue(response.getBody().isPresent());
    }

    @Test
    public void testFindAllFailsIfNotAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        final HttpRequest<?> request = HttpRequest.GET(String.format("")).basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("Internal Server Error: You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.UNAUTHORIZED,  responseException.getStatus());
    }

    @Test
    public void testFindByCheckInId() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        String uploadDocID = "1Ji7hrjp4c2-vBPYm6kyjkAyXI724Bvgp";

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckinDocument cd = createACustomCheckInDocument(checkin, uploadDocID);

        final HttpRequest<?> request= HttpRequest.GET(String.format("/?id=%s", checkin.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<FileInfoDTO>> response = client.toBlocking().exchange(request, Argument.setOf(FileInfoDTO.class));

        FileInfoDTO result = response.body().iterator().next();
        assertNotNull(result);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(uploadDocID, result.getFileId());
        assertEquals(checkin.getId(), result.getCheckInId());
    }

    @Test
    public void testFindByCheckInIdFailsForUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?id=%s", checkin.getId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("Internal Server Error: You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.UNAUTHORIZED,  responseException.getStatus());
    }

    @Test
    public void testFindByCheckInIdThrowsExceptionIfDocDoesNotExist() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkin  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckinDocument cd = createADefaultCheckInDocument(checkin);

        final HttpRequest<?> request= HttpRequest.GET(String.format("/?id=%s", checkin.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("Error occurred while retrieving files from Google Drive.", error);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,  responseException.getStatus());
    }
}
