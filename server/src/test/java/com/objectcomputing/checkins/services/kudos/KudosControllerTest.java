package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.SlackPosterReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientServicesImpl;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
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

// Disabled in nativetest due to a ReflectiveOperationException from Gson
// when attempting to post public Kudos to Slack.
@DisabledInNativeImage
@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
@Property(name = "replace.slackposter", value = StringUtils.TRUE)
class KudosControllerTest extends TestContainersSuite implements KudosFixture, TeamFixture, RoleFixture {
    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    private SlackPosterReplacement slackPoster;

    @Inject
    @Client("/services/kudos")
    HttpClient httpClient;

    @Inject
    CheckInsConfiguration checkInsConfiguration;

    BlockingHttpClient client;

    private String message;
    private MemberProfile sender;
    private MemberProfile recipient;
    private MemberProfile other;
    private MemberProfile admin;
    private UUID senderId;
    private String senderWorkEmail;
    private List<MemberProfile> recipientMembers;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();
        client = httpClient.toBlocking();

        sender = createADefaultMemberProfile();
        senderId = sender.getId();
        senderWorkEmail = sender.getWorkEmail();

        recipient = createASecondDefaultMemberProfile();
        recipientMembers = List.of(recipient);

        admin = createAThirdDefaultMemberProfile();
        assignAdminRole(admin);

        other = createAnotherSupervisor();

        Team team = createDefaultTeam();
        teamId = team.getId();

        message = "Kudos!";
        emailSender.reset();
        slackPoster.reset();
    }

    @ParameterizedTest
    @CsvSource({
            "false, false",
            "false, true",
            "true, false",
            "true, true"
    })
    Kudos testCreateKudos(boolean supplyTeam, boolean publiclyVisible) {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(
                message,
                senderId,
                supplyTeam ? teamId : null,
                publiclyVisible,
                recipientMembers
        );

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("/", kudosCreateDTO).basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpResponse<Kudos> httpResponse = client.exchange(request, Kudos.class);

        final Kudos kudos = httpResponse.body();
        assertEquals(message, kudos.getMessage());
        assertEquals(publiclyVisible, kudos.getPubliclyVisible());
        assertEquals(senderId, kudos.getSenderId());
        assertEquals(supplyTeam ? teamId : null, kudos.getTeamId());
        assertEquals(LocalDate.now(), kudos.getDateCreated());
        if (publiclyVisible) {
            assertNull(kudos.getDateApproved());
        }

        List<KudosRecipient> kudosRecipients = findKudosRecipientByKudosId(kudos.getId());
        assertEquals(1, kudosRecipients.size());
        assertEquals(recipientMembers.getFirst().getId(), kudosRecipients.getFirst().getMemberId());

        if (publiclyVisible) {
            // Admins receive email
            assertEquals(List.of(
                            "SEND_EMAIL",
                            sender.getFirstName() + " " + sender.getLastName(),
                            senderWorkEmail,
                            KudosServicesImpl.KUDOS_EMAIL_SUBJECT,
                            KudosServicesImpl.getAdminEmailContent(checkInsConfiguration),
                            admin.getWorkEmail()
                    ),
                    emailSender.events.getFirst()
            );
        } else {
            // Receiver of kudos receives email
            assertEquals(List.of(
                            "SEND_EMAIL",
                            sender.getFirstName() + " " + sender.getLastName(),
                            senderWorkEmail,
                            KudosRecipientServicesImpl.KUDOS_EMAIL_SUBJECT,
                            message,
                            recipientMembers.getFirst().getWorkEmail()
                    ),
                    emailSender.events.getFirst()
            );
        }
        return kudos;
    }

    @Test
    void testCreateKudosWithNonExistentSenderId() {
        UUID nonExistentSenderId = UUID.randomUUID();
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, nonExistentSenderId, null, true, recipientMembers);

        // The sender does not exist, so they do not have an email address to
        // provide in basicAuth().
        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        String expectedMessage = "Kudos sender %s does not exist".formatted(nonExistentSenderId);
        assertEquals(expectedMessage, responseException.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testCreateKudosWithNonExistentTeamId() {
        UUID nonExistentTeamId = UUID.randomUUID();
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, nonExistentTeamId, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        String expectedMessage = "Team %s does not exist".formatted(nonExistentTeamId);
        assertEquals(expectedMessage, responseException.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testCreateKudosWithBlankMessage() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO("", senderId, null, true, recipientMembers);

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Bad Request", responseException.getMessage());
        String body = responseException.getResponse().getBody(String.class).get();
        assertTrue(body.contains("kudos.message: must not be blank"), body + " should contain 'kudos.message: must not be blank");
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testCreateKudosWithEmptyRecipientMembers() {
        KudosCreateDTO kudosCreateDTO = new KudosCreateDTO(message, senderId, null, true, Collections.emptyList());

        HttpRequest<KudosCreateDTO> request = HttpRequest.POST("", kudosCreateDTO).basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Kudos must contain at least one recipient", responseException.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testApproveKudos() throws JsonProcessingException {
        Kudos kudos = createPublicKudos(senderId);
        assertNull(kudos.getDateApproved());
        KudosRecipient recipient = createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        final HttpRequest<Kudos> request = HttpRequest.PUT("/approve", kudos).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.exchange(request, Kudos.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(LocalDate.now(), response.body().getDateApproved());
        assertEquals(1, emailSender.events.size());
        assertEquals(List.of(
                        "SEND_EMAIL",
                        sender.getFirstName() + " " + sender.getLastName(),
                        senderWorkEmail,
                        KudosServicesImpl.KUDOS_EMAIL_SUBJECT,
                        KudosServicesImpl.getApprovalEmailContent(checkInsConfiguration),
                        recipientMembers.getFirst().getWorkEmail()
                ),
                emailSender.events.getFirst()
        );

        // Check the posted slack block
        assertEquals(1, slackPoster.posted.size());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode posted = mapper.readTree(slackPoster.posted.get(0));

        assertEquals(JsonNodeType.OBJECT, posted.getNodeType());
        JsonNode blocks = posted.get("blocks");
        assertEquals(JsonNodeType.ARRAY, blocks.getNodeType());

        var iter = blocks.elements();
        assertTrue(iter.hasNext());
        JsonNode block = iter.next();

        assertEquals(JsonNodeType.OBJECT, block.getNodeType());
        JsonNode elements = block.get("elements");
        assertEquals(JsonNodeType.ARRAY, elements.getNodeType());

        iter = elements.elements();
        assertTrue(iter.hasNext());
        JsonNode element = iter.next();

        assertEquals(JsonNodeType.OBJECT, element.getNodeType());
        JsonNode innerElements = element.get("elements");
        assertEquals(JsonNodeType.ARRAY, innerElements.getNodeType());

        iter = innerElements.elements();
        assertTrue(iter.hasNext());

        // The real SlackPoster will look up user ids in Slack and use those in
        // the posted message.  Failing the lookup, it will use @<full name>.
        String from = "@" + MemberProfileUtils.getFullName(sender);
        String to = "@" + MemberProfileUtils.getFullName(recipientMembers.get(0));
        boolean foundFrom = false;
        boolean foundTo = false;
        while(iter.hasNext()) {
            element = iter.next();
            assertEquals(JsonNodeType.OBJECT, element.getNodeType());
            String value = element.get("text").asText();
            if (value.equals(from)) {
                foundFrom = true;
            } else if (value.equals(to)) {
                foundTo = true;
            }
        }
        assertTrue(foundFrom && foundTo);
    }

    @Test
    void testApproveNonExistentKudos() {
        Kudos kudos = createADefaultKudos(senderId);
        UUID nonExistentKudosId = UUID.randomUUID();
        kudos.setId(nonExistentKudosId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("/approve", kudos).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Kudos with id %s does not exist".formatted(nonExistentKudosId), responseException.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testApproveAlreadyApprovedKudos() {
        Kudos kudos = createApprovedKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("/approve", kudos).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Kudos with id %s has already been approved".formatted(kudos.getId()), responseException.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testApproveKudosWithoutAdministerPermission() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<Kudos> request = HttpRequest.PUT("/approve", kudos).basicAuth(senderWorkEmail, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, Kudos.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testGetKudosById() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId())).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
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

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", kudos.getId())).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, KudosResponseDTO.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("Could not find recipients for kudos with id %s".formatted(kudos.getId()), responseException.getMessage());
    }

    @Test
    void testGetKudosByNonExistentId() {
        UUID nonExistentId = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", nonExistentId))
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.exchange(request, KudosResponseDTO.class);
        });

        String expectedMessage = "Kudos with id %s does not exist".formatted(nonExistentId);
        assertEquals(expectedMessage, responseException.getMessage());
    }

    @Test
    void testGetKudosByIdWithoutAdminRole() {
        Kudos kudos = createADefaultKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        // This should fail because the user making this request is not an
        // admin, the sender or recipient.
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
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

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
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());
        // This should fail because the user making this request is not an
        // admin, the sender or recipient.
        final HttpRequest<?> request = HttpRequest.GET("/%s".formatted(kudos.getId())).basicAuth("", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, KudosResponseDTO.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
    }

    @Test
    void testGetApprovedKudosByIdWithoutAdminRoleByRecipient() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        final HttpRequest<?> request = HttpRequest.GET("/%s".formatted(kudos.getId())).basicAuth(recipientMembers.getFirst().getWorkEmail(), MEMBER_ROLE);
        HttpResponse<KudosResponseDTO> response = client.exchange(request, KudosResponseDTO.class);

        assertEquals(OK, response.getStatus());
        KudosResponseDTO kudosResponseDTO = response.body();
        assertEquals(kudos.getId(), kudosResponseDTO.getId());
        assertEquals(kudos.getMessage(), kudosResponseDTO.getMessage());
        assertEquals(kudos.getSenderId(), kudosResponseDTO.getSenderId());
        assertEquals(kudos.getDateCreated(), kudosResponseDTO.getDateCreated());
        assertEquals(kudos.getDateApproved(), kudosResponseDTO.getDateApproved());
        assertEquals(kudos.getPubliclyVisible(), kudosResponseDTO.getPubliclyVisible());
        assertEquals(List.of(recipientMembers.getFirst()), kudosResponseDTO.getRecipientMembers());
    }

    @Test
    void testGetKudosWithRecipientId() {
        Kudos kudos = createApprovedKudos(senderId);
        UUID recipientId = recipientMembers.getFirst().getId();
        createKudosRecipient(kudos.getId(), recipientId);

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?recipientId=%s", recipientId)).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
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
    void testGetKudosWithUnknownRecipientId() {
        Kudos kudos = createApprovedKudos(senderId);
        UUID recipientId = recipientMembers.getFirst().getId();
        createKudosRecipient(kudos.getId(), recipientId);

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?recipientId=%s", UUID.randomUUID())).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<List<KudosResponseDTO>> response = client.exchange(request, Argument.listOf(KudosResponseDTO.class));

        assertEquals(OK, response.getStatus());
        assertEquals(0, response.body().size());
    }

    @Test
    void testGetMultipleKudos() {
        UUID recipientId = recipientMembers.getFirst().getId();
        UUID someOtherRecipientId = memberWithoutBoss("tim").getId();

        Kudos kudos = createApprovedKudos(senderId);
        Kudos kudos2 = createApprovedKudos(senderId);
        Kudos kudos3 = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientId);
        createKudosRecipient(kudos2.getId(), recipientId);
        createKudosRecipient(kudos3.getId(), someOtherRecipientId);

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?recipientId=%s", recipientId)).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<List<KudosResponseDTO>> response = client.exchange(request, Argument.listOf(KudosResponseDTO.class));

        assertEquals(OK, response.getStatus());
        List<KudosResponseDTO> body = response.body();
        assertEquals(2, body.size());
        assertEquals(List.of(kudos.getId(), kudos2.getId()), List.of(body.get(0).getId(), body.get(1).getId()));
    }

    @Test
    void testGetKudosWithSenderId() {
        Kudos kudos = createApprovedKudos(senderId);
        createKudosRecipient(kudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?senderId=%s", senderId)).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<List<KudosResponseDTO>> response = client.exchange(request, Argument.listOf(KudosResponseDTO.class));

        assertEquals(OK, response.getStatus());
        assertEquals(1, response.body().size());
        KudosResponseDTO element = response.body().getFirst();
        assertEquals(kudos.getId(), element.getId());
        assertEquals(kudos.getMessage(), element.getMessage());
        assertEquals(kudos.getSenderId(), element.getSenderId());
        assertEquals(kudos.getDateCreated(), element.getDateCreated());
        assertEquals(kudos.getDateApproved(), element.getDateApproved());
        assertEquals(kudos.getPubliclyVisible(), element.getPubliclyVisible());
        assertEquals(List.of(recipientMembers.getFirst()), element.getRecipientMembers());
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void testGetKudosWithIsPending(boolean isPending) {
        Kudos unapprovedKudos = createPublicKudos(senderId);
        Kudos approvedKudos = createApprovedKudos(senderId);

        createKudosRecipient(unapprovedKudos.getId(), recipientMembers.getFirst().getId());
        createKudosRecipient(approvedKudos.getId(), recipientMembers.getFirst().getId());

        MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?isPending=%s", isPending)).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<List<KudosResponseDTO>> response = client.exchange(request, Argument.listOf(KudosResponseDTO.class));

        var expected = isPending ? unapprovedKudos : approvedKudos;

        assertEquals(OK, response.getStatus());
        assertEquals(1, response.body().size());
        KudosResponseDTO element = response.body().getFirst();
        assertEquals(expected.getId(), element.getId());
        assertEquals(expected.getMessage(), element.getMessage());
        assertEquals(expected.getSenderId(), element.getSenderId());
        assertEquals(expected.getDateCreated(), element.getDateCreated());
        assertEquals(expected.getDateApproved(), element.getDateApproved());
        assertEquals(expected.getPubliclyVisible(), element.getPubliclyVisible());
        assertEquals(List.of(recipientMembers.getFirst()), element.getRecipientMembers());
    }

    @Test
    void testDeleteKudos() {
        Kudos kudos = createADefaultKudos(senderId);

        final HttpRequest<Object> request = HttpRequest.DELETE("/%s".formatted(kudos.getId())).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.exchange(request);

        assertEquals(NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteKudosWithNonExistentKudosId() {
        UUID nonExistentKudosId = UUID.randomUUID();

        final HttpRequest<Object> request = HttpRequest.DELETE("/%s".formatted(nonExistentKudosId)).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("Kudos with id %s does not exist".formatted(nonExistentKudosId), responseException.getMessage());
    }

    @Test
    void testDeleteKudosWithoutAdministerPermission() {
        Kudos kudos = createADefaultKudos(senderId);

        HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s", kudos.getId())).basicAuth(recipient.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.exchange(request));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "false, false",
            "false, true",
            "true, false",
            "true, true"
    })
    void testUpdateKudos(boolean supplyTeam, boolean publiclyVisible) {
        // Create a kudos
        final Kudos kudos = testCreateKudos(supplyTeam, publiclyVisible);

        // Set of changes to make.
        final String message = "New kudos message";
        final boolean visible = !publiclyVisible;
        final List<MemberProfile> members = new ArrayList<>();
        members.add(other);
        if (!supplyTeam || publiclyVisible) {
            // On some tests, retain the original recipient.
            members.add(recipient);
        }

        // Create the DTO
        KudosUpdateDTO proposed = new KudosUpdateDTO(kudos.getId(), message,
                                                     visible, members);

        // Make the call
        final HttpRequest<KudosUpdateDTO> request =
            HttpRequest.PUT("", proposed)
                       .basicAuth(senderWorkEmail, MEMBER_ROLE);
        final HttpResponse<Kudos> response = client.exchange(request,
                                                             Kudos.class);
        assertEquals(HttpStatus.OK, response.getStatus());

        final Kudos updated = response.body();
        assertEquals(message, updated.getMessage());
        assertEquals(visible, updated.getPubliclyVisible());

        if (visible) {
            // Public kudos should not be approved.
            assertNull(updated.getDateApproved());
        } else {
            // Private kudos should be approved.
            assertNotNull(updated.getDateApproved());
        }

        final List<KudosRecipient> kudosRecipients =
                    findKudosRecipientByKudosId(updated.getId());
        assertEquals(members.size(), kudosRecipients.size());
        for (MemberProfile member : members) {
            boolean found = false;
            for (KudosRecipient recipient : kudosRecipients) {
                if (recipient.getMemberId().equals(member.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

}
