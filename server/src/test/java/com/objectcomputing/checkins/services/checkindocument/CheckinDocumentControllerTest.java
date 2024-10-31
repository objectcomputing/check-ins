package com.objectcomputing.checkins.services.checkindocument;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInDocumentFixture;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckinDocumentControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture,
        RoleFixture, CheckInFixture, CheckInDocumentFixture {

    @Inject
    @Client("/services/checkin-documents")
    HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testCreateACheckinDocument() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(checkIn.getId());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("/", checkinDocumentCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        final HttpResponse<CheckinDocument> response = client.toBlocking().exchange(request, CheckinDocument.class);

        CheckinDocument checkinDocument = response.body();
        assertNotNull(response);
        assertEquals(checkinDocumentCreateDTO.getCheckinsId(), checkinDocument.getCheckinsId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkinDocument.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidCheckinDocument() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinDocument] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateCheckinDocumentThrowsExceptionForMemberRole() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role authRole = assignMemberRole(memberProfile);
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(checkIn.getId());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<?> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(memberProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void testCreateACheckInDocumentForNonExistingCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(UUID.randomUUID());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("CheckIn %s doesn't exist", checkinDocumentCreateDTO.getCheckinsId()), error);

    }

    @Test
    void testCreateACheckInDocumentForExistingDocumentId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(checkIn.getId());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("CheckinDocument with document ID %s already exists", checkinDocumentCreateDTO.getUploadDocId()), error);

    }

    @Test
    void testFindCheckinDocument() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=%s", checkinDocument.getCheckinsId())).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        final HttpResponse<Set<CheckinDocument>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class));

        assertEquals(Set.of(checkinDocument), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindCheckinDocumentNull() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=" + null)).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testFindCheckinDocumentThrowsExceptionForMemberRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignMemberRole(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=" + checkinDocument.getCheckinsId())).basicAuth(memberProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void testUpdateCheckinDocument() {


        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        final HttpResponse<CheckinDocument> response = client.toBlocking().exchange(request, CheckinDocument.class);

        assertEquals(checkinDocument, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkinDocument.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testUpdateAnInvalidCheckinDocument() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setCheckinsId(null);
        checkinDocument.setUploadDocId(null);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinDocument] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateCheckinDocumentThrowsExceptionForMemberRole() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignMemberRole(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(memberProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateNotExistingCheckInDocument() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setId(UUID.randomUUID());

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument)
                .basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", checkinDocument.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setCheckinsId(UUID.randomUUID());

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument)
                .basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = assignPdlRole(memberProfileForPDL);

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(memberProfileForPDL.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }     

    @Test
    void deleteCheckinDocumentThrowsExceptionForMemberRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role authRole = assignMemberRole(memberProfile);

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(memberProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

    }

    @Test
    void deleteCheckinDocumentIfAdmin() {
        MemberProfile unrelatedMember = createAnUnrelatedUser();
        Role authRole = assignAdminRole(unrelatedMember);

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);

        final HttpRequest<?> request = HttpRequest.DELETE(checkinDocument.getCheckinsId().toString()).basicAuth(unrelatedMember.getWorkEmail(), authRole.getRole());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

    }

    @Test
    void deleteCheckinDocumentNotExistCheckIn() {
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        Role authRole = assignAdminRole(unrelatedUser);

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinDocument checkinDocument = createADefaultCheckInDocument(checkIn);
        checkinDocument.setCheckinsId(UUID.randomUUID());

        final HttpRequest<?> request = HttpRequest.DELETE(checkinDocument.getCheckinsId().toString()).basicAuth(unrelatedUser.getWorkEmail(), authRole.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckinDocument with CheckinsId %s does not exist", checkinDocument.getCheckinsId()), error);
        assertEquals(request.getPath(), href);

    }
}
