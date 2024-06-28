package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.HttpStatus.NO_CONTENT;
import static io.micronaut.http.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KudosControllerTest extends TestContainersSuite implements KudosFixture, TeamFixture {

    @Inject
    @Client("/services/kudos")
    HttpClient client;

    private String message;
    private UUID senderId;
    private String senderWorkEmail;
    private List<MemberProfile> recipientMembers;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createASecondDefaultMemberProfile();
        Team team = createDefaultTeam();
        message = "Kudos!";
        senderId = sender.getId();
        senderWorkEmail = sender.getWorkEmail();
        recipientMembers = List.of(recipient);
        teamId = team.getId();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvSource({"false, false", "false, true", "true, false", "true, true"})
    void testCreateKudos(boolean supplyTeam, boolean publiclyVisible) {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, supplyTeam ? teamId : null, publiclyVisible, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpResponse<Kudos> httpResponse = client.toBlocking().exchange(request, Kudos.class);

        assertNotNull(httpResponse);
        Optional<Kudos> body = httpResponse.getBody();
        assertTrue(body.isPresent());
        Kudos kudos = body.get();
        assertNotNull(kudos);
        assertEquals(message, kudos.getMessage());
        assertEquals(publiclyVisible, kudos.getPubliclyVisible());
        assertEquals(senderId, kudos.getSenderId());
        assertEquals(supplyTeam ? teamId : null, kudos.getTeamId());
        assertEquals(LocalDate.now(), kudos.getDateCreated());
        assertNull(kudos.getDateApproved());

        List<KudosRecipient> kudosRecipients = findKudosRecipientByKudosId(kudos.getId());
        assertNotNull(kudosRecipients);
        assertEquals(1, kudosRecipients.size());
        KudosRecipient kudosRecipient = kudosRecipients.getFirst();
        assertEquals(recipientMembers.getFirst().getId(), kudosRecipient.getMemberId());
    }

    @Test
    void testCreateKudosWithoutAdminRole() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        assertEquals("You are not authorized to do this operation", responseException.getMessage());
    }

    @Test
    void testCreateKudosWithNonExistentSenderId() {
        UUID nonExistentSenderId = UUID.randomUUID();
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, nonExistentSenderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        String expectedMessage = MessageFormat.format("Kudos sender {0} does not exist", nonExistentSenderId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testCreateKudosWithNonExistentTeamId() {
        UUID nonExistentTeamId = UUID.randomUUID();
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, nonExistentTeamId, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        String expectedMessage = MessageFormat.format("Team {0} does not exist", nonExistentTeamId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testCreateKudosWithBlankMessage() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO("", senderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        assertEquals("Bad Request", responseException.getMessage());
    }

    @Test
    void testCreateKudosWithEmptyRecipientMembers() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, null, true, Collections.emptyList());

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        assertEquals("Kudos must contain at least one recipient", responseException.getMessage());
    }

    @Test
    void testApproveKudos() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos)
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.toBlocking().exchange(request, Kudos.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        Kudos returnedKudos = response.body();
        assertNotNull(returnedKudos);
        assertNotNull(returnedKudos.getDateApproved());
    }

    @Test
    void testApproveNonExistentKudos() {
        Kudos kudos = createADefaultKudos(senderId);
        UUID nonExistentKudosId = UUID.randomUUID();
        kudos.setId(nonExistentKudosId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos)
                .basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        String expectedMessage = MessageFormat.format("Kudos with id {0} does not exist", nonExistentKudosId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testApproveAlreadyApprovedKudos() {
        Kudos kudos = createApprovedKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos)
                .basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        String expectedMessage = MessageFormat.format("Kudos with id {0} has already been approved", kudos.getId());
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testApproveKudosWithoutAdminRole() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos)
                .basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Kudos.class);
        });

        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testGetKudosById() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth("", ADMIN_ROLE);
        HttpResponse<KudosResponseDTO> response = client.toBlocking().exchange(request, KudosResponseDTO.class);

        assertEquals(OK, response.getStatus());
    }

    @Test
    void testGetKudosByIdWithNoRecipients() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, KudosResponseDTO.class);
        });

        String expectedMessage = MessageFormat.format("Could not find recipients for kudos with id {0}", kudos.getId());
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testGetKudosByNonExistentId() {
        UUID nonExistentId = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", nonExistentId))
                .basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, KudosResponseDTO.class);
        });

        String expectedMessage = MessageFormat.format("Kudos with id {0} does not exist", nonExistentId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testGetKudosByIdWithoutAdminRole() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, KudosResponseDTO.class);
        });

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
    }

    @Test
    void testGetKudosByIdWithoutAdminRoleBySender() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpResponse<KudosResponseDTO> response = client.toBlocking().exchange(request, KudosResponseDTO.class);

        assertEquals(OK, response.getStatus());
        Optional<KudosResponseDTO> body = response.getBody();
        assertTrue(body.isPresent());
        KudosResponseDTO kudosResponseDTO = body.get();
        assertNotNull(kudosResponseDTO);
    }

    @Test
    void testGetApprovedKudosByIdWithoutAdminRole() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, KudosResponseDTO.class);
        });

        assertEquals("You are not authorized to perform this operation", responseException.getMessage());
    }

    @Test
    void testGetApprovedKudosByIdWithoutAdminRoleByRecipient() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth(recipientMembers.get(0).getWorkEmail(), MEMBER_ROLE);
        HttpResponse<KudosResponseDTO> response = client.toBlocking().exchange(request, KudosResponseDTO.class);

        assertEquals(OK, response.getStatus());
        Optional<KudosResponseDTO> body = response.getBody();
        assertTrue(body.isPresent());
        KudosResponseDTO kudosResponseDTO = body.get();
        assertNotNull(kudosResponseDTO);
    }

    @Test
    void testGetKudosWithRecipientId() {
        Kudos kudos = createApprovedKudos(senderId);
        UUID recipientId = recipientMembers.getFirst().getId();
        createKudosRecipient(kudos.getId(), recipientId);

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?recipientId=%s", recipientId))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<List> response = client.toBlocking().exchange(request, List.class);

        assertEquals(OK, response.getStatus());
        Optional<List> body = response.getBody();
        assertTrue(body.isPresent());
        List list = body.get();
        assertNotNull(list);
    }

    @Test
    void testGetKudosWithSenderId() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?senderId=%s", senderId))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<List> response = client.toBlocking().exchange(request, List.class);

        assertEquals(OK, response.getStatus());
        Optional<List> body = response.getBody();
        assertTrue(body.isPresent());
        List list = body.get();
        assertNotNull(list);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void testGetKudosWithIsPending(boolean isPending) {
        Kudos kudos = isPending ? createPublicKudos(senderId) : createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?isPending=%s", isPending))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<List> response = client.toBlocking().exchange(request, List.class);

        assertEquals(OK, response.getStatus());
        Optional<List> body = response.getBody();
        assertTrue(body.isPresent());
        List list = body.get();
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void testGetKudosWithIsPublic(boolean isPublic) {
        Kudos kudos = isPublic ? createPublicKudos(senderId) : createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?isPublic=%s", isPublic))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<List> response = client.toBlocking().exchange(request, List.class);

        assertEquals(OK, response.getStatus());
        Optional<List> body = response.getBody();
        assertTrue(body.isPresent());
        List list = body.get();
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testDeleteKudos() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s", kudos.getId()))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.toBlocking().exchange(request);

        assertEquals(NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteKudosWithNonExistentKudosId() {
        UUID nonExistentKudosId = UUID.randomUUID();
        final HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s", nonExistentKudosId))
                .basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });

        String expectedMessage = "Kudos with id " + nonExistentKudosId + " does not exist";
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testDeleteKudosWithoutAdminRole() {
        Kudos kudos = createADefaultKudos(senderId);

        HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s", kudos.getId()))
                .basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });

        assertEquals("Forbidden", responseException.getMessage());
    }
}