package com.objectcomputing.checkins.services.checkindocument;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInDocumentFixture;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CheckinDocumentControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, CheckInFixture, CheckInDocumentFixture {

    @Inject
    @Client("/services/checkin-document")
    HttpClient client;

    @Test
    void testCreateACheckinDocument() {

        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(checkIn.getId());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("/", checkinDocumentCreateDTO).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<CheckinDocument> response = client.toBlocking().exchange(request, CheckinDocument.class);

        CheckinDocument checkinDocument = response.body();
        assertNotNull(response);
        assertEquals(checkinDocumentCreateDTO.getCheckinsId(),checkinDocument.getCheckinsId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkinDocument.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidCheckinDocument() {
        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinDocument.checkinsId: must not be null", errorList.get(0));
        assertEquals("checkinDocument.uploadDocId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullCheckinDocument() {
        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinDocument] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateCheckinDocumentThrowsExceptionForMemberRole() {

        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(checkIn.getId());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<?> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void testCreateACheckInDocumentForExistingCheckInId() {
        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(UUID.randomUUID());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("",checkinDocumentCreateDTO).basicAuth(PDL_ROLE,PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("CheckIn %s doesn't exist",checkinDocumentCreateDTO.getCheckinsId()),error);

    }

    @Test
    void testCreateACheckInDocumentForExistingDocumentId() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(checkIn.getId());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("",checkinDocumentCreateDTO).basicAuth(PDL_ROLE,PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("CheckinDocument with document ID %s already exists",checkinDocumentCreateDTO.getUploadDocId()),error);

    }

    @Test
    void testFindCheckinDocument() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=%s", checkinDocument.getCheckinsId())).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<Set<CheckinDocument>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class));

        assertEquals(Set.of(checkinDocument), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindCheckinDocumentNull() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=" + null)).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testFindCheckinDocumentThrowsExceptionForMemberRole() {

        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=" + checkinDocument.getCheckinsId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void testUpdateCheckinDocument() {


        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<CheckinDocument> response = client.toBlocking().exchange(request, CheckinDocument.class);

        assertEquals(checkinDocument, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkinDocument.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testUpdateAnInvalidCheckinDocument() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setCheckinsId(null);
        checkinDocument.setUploadDocId(null);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinDocument.checkinsId: must not be null", errorList.get(0));
        assertEquals("checkinDocument.uploadDocId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateANullCheckinDocument() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinDocument] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateCheckinDocumentThrowsExceptionForMemberRole() {

        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateNotExistingCheckInDocument(){
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setId(UUID.randomUUID());

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument)
                .basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", checkinDocument.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingCheckInId(){
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setCheckinsId(UUID.randomUUID());

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument)
                .basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckIn %s doesn't exist", checkinDocument.getCheckinsId()), error);
        assertEquals(request.getPath(), href);

    }


    @Test
    void deleteCheckinDocumentThrowsException() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<CheckinDocument> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());

    }

    @Test
    void deleteCheckinDocumentThrowsExceptionForPdlRole() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void deleteCheckinDocumentThrowsExceptionForMemberRole() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void deleteCheckinDocumentIfAdmin() {

        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<?> request = HttpRequest.DELETE(checkinDocument.getCheckinsId().toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

    }

    @Test
    void deleteCheckinDocumentNotExistCheckIn() {

        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setCheckinsId(UUID.randomUUID());

        final HttpRequest<?> request = HttpRequest.DELETE(checkinDocument.getCheckinsId().toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckinDocument with CheckinsId %s does not exist", checkinDocument.getCheckinsId()), error);
        assertEquals(request.getPath(), href);

    }
}
