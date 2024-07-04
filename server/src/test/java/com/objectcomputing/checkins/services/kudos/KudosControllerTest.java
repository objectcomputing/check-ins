package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
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
import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
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
    HttpClient httpClient;

    BlockingHttpClient client;

    private String message;
    private UUID senderId;
    private String senderWorkEmail;
    private List<MemberProfile> recipientMembers;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        client = httpClient.toBlocking();
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createASecondDefaultMemberProfile();
        Team team = createDefaultTeam();
        message = "Kudos!";
        senderId = sender.getId();
        senderWorkEmail = sender.getWorkEmail();
        recipientMembers = List.of(recipient);
        teamId = team.getId();
    }

    @ParameterizedTest
    @CsvSource({
            "false, false",
            "false, true",
            "true, false",
            "true, true"
    })
    void testCreateKudos(boolean supplyTeam, boolean publiclyVisible) {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(
                message,
                senderId,
                supplyTeam ? teamId : null,
                publiclyVisible,
                recipientMembers
        );

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("/", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpResponse<Kudos> httpResponse = client.exchange(request, Kudos.class);

        Kudos kudos = httpResponse.body();
        assertEquals(message, kudos.getMessage());
        assertEquals(publiclyVisible, kudos.getPubliclyVisible());
        assertEquals(senderId, kudos.getSenderId());
        assertEquals(supplyTeam ? teamId : null, kudos.getTeamId());
        assertEquals(LocalDate.now(), kudos.getDateCreated());
        assertNull(kudos.getDateApproved());

        List<KudosRecipient> kudosRecipients = findKudosRecipientByKudosId(kudos.getId());
        assertEquals(1, kudosRecipients.size());
        assertEquals(recipientMembers.getFirst().getId(), kudosRecipients.getFirst().getMemberId());
    }

    @Test
    void testCreateKudosWithoutAdminRole() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testCreateKudosWithNonExistentSenderId() {
        UUID nonExistentSenderId = UUID.randomUUID();
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, nonExistentSenderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        String expectedMessage = "Kudos sender %s does not exist".formatted(nonExistentSenderId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testCreateKudosWithNonExistentTeamId() {
        UUID nonExistentTeamId = UUID.randomUUID();
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, nonExistentTeamId, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        String expectedMessage = "Team %s does not exist".formatted(nonExistentTeamId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testCreateKudosWithBlankMessage() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO("", senderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Bad Request", responseException.getMessage());
        String body = responseException.getResponse().getBody(String.class).get();
        assertTrue(body.contains("kudos.message: must not be blank"), body + " should contain 'kudos.message: must not be blank");
    }

    @Test
    void testCreateKudosWithEmptyRecipientMembers() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, null, true, Collections.emptyList());

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Kudos must contain at least one recipient", responseException.getMessage());
    }

    @Test
    void testApproveKudos() {
        Kudos kudos = createADefaultKudos(senderId);
        assertNull(kudos.getDateApproved());

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos).basicAuth("", ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.exchange(request, Kudos.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(LocalDate.now(), response.body().getDateApproved());
    }

    @Test
    void testApproveNonExistentKudos() {
        Kudos kudos = createADefaultKudos(senderId);
        UUID nonExistentKudosId = UUID.randomUUID();
        kudos.setId(nonExistentKudosId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Kudos with id %s does not exist".formatted(nonExistentKudosId), responseException.getMessage());
    }

    @Test
    void testApproveAlreadyApprovedKudos() {
        Kudos kudos = createApprovedKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Kudos with id %s has already been approved".formatted(kudos.getId()), responseException.getMessage());
    }

    @Test
    void testApproveKudosWithoutAdminRole() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("", kudos).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testGetKudosById() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId())).basicAuth("", ADMIN_ROLE);
        HttpResponse<KudosResponseDTO> response = client.exchange(request, KudosResponseDTO.class);

        assertEquals(OK, response.getStatus());
        assertEquals(kudos.getId(), response.body().getId());
        assertEquals(kudos.getMessage(), response.body().getMessage());
        assertEquals(kudos.getSenderId(), response.body().getSenderId());
        assertEquals(kudos.getDateCreated(), response.body().getDateCreated());
        assertEquals(kudos.getDateApproved(), response.body().getDateApproved());
        assertEquals(kudos.getPubliclyVisible(), response.body().getPubliclyVisible());
        assertEquals(List.of(recipientMembers.getFirst()), response.body().getRecipientMembers());
    }

    @Test
    void testGetKudosByIdWithNoRecipients() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId())).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, KudosResponseDTO.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("Could not find recipients for kudos with id %s".formatted(kudos.getId()), responseException.getMessage());
    }

    @Test
    void testGetKudosByNonExistentId() {
        UUID nonExistentId = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", nonExistentId))
                .basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.exchange(request, KudosResponseDTO.class);
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
            client.exchange(request, KudosResponseDTO.class);
        });

        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testGetKudosByIdWithoutAdminRoleBySender() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId()))
                .basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpResponse<KudosResponseDTO> response = client.exchange(request, KudosResponseDTO.class);

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

        final HttpRequest<?> request = HttpRequest.GET("/%s".formatted(kudos.getId())).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, KudosResponseDTO.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testGetApprovedKudosByIdWithoutAdminRoleByRecipient() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.get(0).getId());

        final HttpRequest<?> request = HttpRequest.GET("/%s".formatted(kudos.getId())).basicAuth(recipientMembers.get(0).getWorkEmail(), MEMBER_ROLE);
        HttpResponse<KudosResponseDTO> response = client.exchange(request, KudosResponseDTO.class);

        assertEquals(OK, response.getStatus());
        KudosResponseDTO kudosResponseDTO = response.body();
        assertEquals(kudos.getId(), kudosResponseDTO.getId());
        assertEquals(kudos.getMessage(), kudosResponseDTO.getMessage());
        assertEquals(kudos.getSenderId(), kudosResponseDTO.getSenderId());
        assertEquals(kudos.getDateCreated(), kudosResponseDTO.getDateCreated());
        assertEquals(kudos.getDateApproved(), kudosResponseDTO.getDateApproved());
        assertEquals(kudos.getPubliclyVisible(), kudosResponseDTO.getPubliclyVisible());
        assertEquals(List.of(recipientMembers.get(0)), kudosResponseDTO.getRecipientMembers());
    }

    @Test
    void testGetKudosWithRecipientId() {
        Kudos kudos = createApprovedKudos(senderId);
        UUID recipientId = recipientMembers.getFirst().getId();
        createKudosRecipient(kudos.getId(), recipientId);

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?recipientId=%s", recipientId)).basicAuth("", ADMIN_ROLE);
        final HttpResponse<List<KudosResponseDTO>> response = client.exchange(request, Argument.listOf(KudosResponseDTO.class));

        assertEquals(OK, response.getStatus());
        assertEquals(1, response.body().size());
        KudosResponseDTO element = response.body().getFirst();
        assertEquals(element.getId(), kudos.getId());
        assertEquals(element.getMessage(), kudos.getMessage());
        assertEquals(element.getSenderId(), kudos.getSenderId());
        assertEquals(element.getDateCreated(), kudos.getDateCreated());
        assertEquals(element.getDateApproved(), kudos.getDateApproved());
        assertEquals(element.getPubliclyVisible(), kudos.getPubliclyVisible());
        assertEquals(List.of(recipientMembers.getFirst()), element.getRecipientMembers());
    }

    @Test
    void testGetKudosWithSenderId() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?senderId=%s", senderId))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<List<KudosResponseDTO>> response = client.exchange(request, Argument.listOf(KudosResponseDTO.class));

        assertEquals(OK, response.getStatus());
        assertEquals(1, response.body().size());
        KudosResponseDTO element = response.body().getFirst();
        assertEquals(element.getId(), kudos.getId());
        assertEquals(element.getMessage(), kudos.getMessage());
        assertEquals(element.getSenderId(), kudos.getSenderId());
        assertEquals(element.getDateCreated(), kudos.getDateCreated());
        assertEquals(element.getDateApproved(), kudos.getDateApproved());
        assertEquals(element.getPubliclyVisible(), kudos.getPubliclyVisible());
        assertEquals(List.of(recipientMembers.getFirst()), element.getRecipientMembers());
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void testGetKudosWithIsPending(boolean isPending) {
        Kudos kudos = isPending ? createPublicKudos(senderId) : createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?isPending=%s", isPending))
                .basicAuth("", ADMIN_ROLE);
        final HttpResponse<List> response = client.exchange(request, List.class);

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

        final HttpRequest<Object> request = HttpRequest.DELETE("/%s".formatted(kudos.getId())).basicAuth("", ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.exchange(request);

        assertEquals(NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteKudosWithNonExistentKudosId() {
        UUID nonExistentKudosId = UUID.randomUUID();

        final HttpRequest<Object> request = HttpRequest.DELETE("/%s".formatted(nonExistentKudosId)).basicAuth("", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("Kudos with id %s does not exist".formatted(nonExistentKudosId), responseException.getMessage());
    }

    @Test
    void testDeleteKudosWithoutAdminRole() {
        Kudos kudos = createADefaultKudos(senderId);

        HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s", kudos.getId())).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }
}