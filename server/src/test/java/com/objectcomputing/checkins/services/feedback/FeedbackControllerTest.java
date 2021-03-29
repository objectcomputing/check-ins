package com.objectcomputing.checkins.services.feedback;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.FeedbackFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class FeedbackControllerTest extends TestContainersSuite implements FeedbackFixture, RoleFixture {

    @Inject
    @Client("/services/feedback")
    private HttpClient client;

    private Feedback setupGet(boolean confidential) {
        final MemberProfile from = getMemberProfileRepository().save(mkMemberProfile("1"));
        final MemberProfile to = getMemberProfileRepository().save(mkMemberProfile("2"));
        return createFeedback("Some constructive feedback", to, from, confidential);
    }

    @Test
    public void testGetSucceedAdmin() {
        final Feedback feedback = setupGet(true);
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        final Role role = createDefaultAdminRole(admin);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", feedback.getId()))
                .basicAuth(admin.getWorkEmail(), role.getRole().name());
        final HttpResponse<FeedbackResponseDTO> response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEntityEqualsResponse(feedback, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetSucceedNotAdminPublicFeedback() {
        final Feedback feedback = setupGet(false);
        final MemberProfile someone = getMemberProfileRepository().save(mkMemberProfile("someone"));

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedback.getId()))
                .basicAuth(someone.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackResponseDTO> response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEntityEqualsResponse(feedback, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetSucceedNotAdminPrivateFeedback() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        final MemberProfile bob = getMemberProfileRepository().save(mkMemberProfile("Bob"));
        final Feedback feedback = createFeedback("Feedback from Alice to Bob", bob, alice, true);

        HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedback.getId()))
                .basicAuth(alice.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        HttpResponse<FeedbackResponseDTO> response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEntityEqualsResponse(feedback, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        request = HttpRequest.GET(String.format("%s", feedback.getId()))
                .basicAuth(bob.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEntityEqualsResponse(feedback, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetIdNotFound() {
        final MemberProfile alex = getMemberProfileRepository().save(mkMemberProfile("Alex"));
        final UUID someId = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", someId))
                .basicAuth(alex.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("No feedback with id %s", someId), error);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testGetPermissionDenied() {
        final Feedback feedback = setupGet(true);
        final MemberProfile someone = getMemberProfileRepository().save(mkMemberProfile("someone"));

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedback.getId()))
                .basicAuth(someone.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("You are not authorized to read this feedback", error);
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    private List<Feedback> setupFind() {
        final MemberProfile from = getMemberProfileRepository().save(mkMemberProfile("from"));
        final MemberProfile to = getMemberProfileRepository().save(mkMemberProfile("to"));
        final Feedback fb1 = createFeedback("A public feedback", to, from, false);
        final Feedback fb2 = createFeedback("A private feedback", to, from, true);
        final ArrayList<Feedback> result = new ArrayList<>();
        result.add(fb1);
        result.add(fb2);
        return result;
    }

    @Test
    public void testFindAllAdmin() {
        final List<Feedback> feedbacks = setupFind();
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        final Role role = createDefaultAdminRole(admin);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(admin.getWorkEmail(), role.getRole().name());
        final HttpResponse<List<FeedbackResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackResponseDTO.class));

        assertNotNull(response.getBody().get());
        for (FeedbackResponseDTO dto : response.getBody().get()) {
            if (dto.getId().equals(feedbacks.get(0).getId())) {
                assertEntityEqualsResponse(feedbacks.get(0), dto);
            } else {
                assertEquals(feedbacks.get(1).getId(), dto.getId());
                assertEntityEqualsResponse(feedbacks.get(1), dto);
            }
        }
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testFindAllNotAdmin() {
        final List<Feedback> feedbacks = setupFind();
        final MemberProfile someone = getMemberProfileRepository().save(mkMemberProfile("someone"));

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(someone.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackResponseDTO.class));

        assertNotNull(response.getBody().get());
        assertEntityEqualsResponse(feedbacks.get(0), response.body().get(0));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testFindBySentBy() {
        final MemberProfile from = getMemberProfileRepository().save(mkMemberProfile("from"));
        final MemberProfile to = getMemberProfileRepository().save(mkMemberProfile("to"));
        final Feedback feedback = createFeedback("A public feedback", to, from, false);
        final MemberProfile someone = getMemberProfileRepository().save(mkMemberProfile("someone"));

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?sentBy=%s", from.getId()))
                .basicAuth(someone.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackResponseDTO.class));

        assertEntityEqualsResponse(feedback, response.body().get(0));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testFindBySentTo() {
        final MemberProfile from = getMemberProfileRepository().save(mkMemberProfile("from"));
        final MemberProfile to = getMemberProfileRepository().save(mkMemberProfile("to"));
        final Feedback feedback = createFeedback("A private feedback", to, from, true);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?sentTo=%s", to.getId()))
                .basicAuth(from.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackResponseDTO.class));

        assertEntityEqualsResponse(feedback, response.body().get(0));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testFindByConfidential() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        final MemberProfile bob = getMemberProfileRepository().save(mkMemberProfile("Bob"));
        final Feedback toAlice = createFeedback("A private feedback", alice, bob, true);
        final Feedback toBob = createFeedback("Another private feedback", bob, alice, true);
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("boss"));
        final Role role = createDefaultAdminRole(admin);

        HttpRequest<?> request = HttpRequest.GET(String.format("/?confidential=true"))
                .basicAuth(admin.getWorkEmail(), role.getRole().name());
        HttpResponse<List<FeedbackResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackResponseDTO.class));

        for (FeedbackResponseDTO dto : response.body()) {
            if (dto.getId().equals(toAlice.getId())) {
                assertEntityEqualsResponse(toAlice, dto);
            } else {
                assertEquals(dto.getId(), toBob.getId());
                assertEntityEqualsResponse(toBob, dto);
            }
        }
        assertEquals(HttpStatus.OK, response.getStatus());

        request = HttpRequest.GET(String.format("/?confidential=false"))
                .basicAuth(admin.getWorkEmail(), role.getRole().name());
        response = client.toBlocking().exchange(request, Argument.listOf(FeedbackResponseDTO.class));

        assertEquals(0, response.body().size());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testPostSucceed() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        final MemberProfile bob = getMemberProfileRepository().save(mkMemberProfile("Bob"));
        final FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setContent("Feedback from Alice to Bob");
        dto.setSentBy(alice.getId());
        dto.setSentTo(bob.getId());
        dto.setConfidential(true);
        dto.setCreatedOn(LocalDateTime.now());

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(RoleType.Constants.MEMBER_ROLE, RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackResponseDTO> response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(response.body().getSentBy(), alice.getId());
        assertEquals(response.body().getSentTo(), bob.getId());
        assertEquals(response.body().getContent(), dto.getContent());
        assertEquals(response.body().getConfidential(), dto.getConfidential());
    }

    @Test
    public void testPostInvalidId() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        final FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setContent("Alice's feedback");
        dto.setSentBy(alice.getId());
        dto.setSentTo(UUID.randomUUID());
        dto.setConfidential(false);
        dto.setCreatedOn(LocalDateTime.now());

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(RoleType.Constants.MEMBER_ROLE, RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        final String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("Either the sender id or the receiver id is invalid", error);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void testPostValidationFailed() {
        final HttpRequest<?> request = HttpRequest.POST("", new FeedbackCreateDTO())
                .basicAuth(RoleType.Constants.MEMBER_ROLE, RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        final JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        final JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        final List<String> errorList = List.of(errors.get(0).get("message").asText(),
                errors.get(1).get("message").asText(),
                errors.get(2).get("message").asText(),
                errors.get(3).get("message").asText(),
                errors.get(4).get("message").asText())
                .stream().sorted().collect(Collectors.toList());

        assertEquals(5, errors.size());
        assertEquals("requestBody.confidential: must not be null", errorList.get(0));
        assertEquals("requestBody.content: must not be blank", errorList.get(1));
        assertEquals("requestBody.createdOn: must not be null", errorList.get(2));
        assertEquals("requestBody.sentBy: must not be null", errorList.get(3));
        assertEquals("requestBody.sentTo: must not be null", errorList.get(4));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void testPostNullBody() {
        final HttpRequest<?> request = HttpRequest.POST("", "")
                .basicAuth(RoleType.Constants.MEMBER_ROLE, RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        final JsonNode errors = Objects.requireNonNull(body).get("message");
        final JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [requestBody] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void testPutSucceed() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        final MemberProfile bob = getMemberProfileRepository().save(mkMemberProfile("Bob"));
        final Feedback feedback = createFeedback("Alice's feedback", bob, alice, true);

        // Update by admin
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        Role role = createDefaultAdminRole(admin);

        final FeedbackUpdateDTO dto = new FeedbackUpdateDTO();
        dto.setId(feedback.getId());
        dto.setContent("Alice's another feedback");
        dto.setSentBy(alice.getId());
        dto.setSentTo(bob.getId());
        dto.setConfidential(false);
        dto.setCreatedOn(feedback.getCreatedOn());
        dto.setUpdatedOn(LocalDateTime.now());

        HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(admin.getWorkEmail(), role.getRole().name());
        HttpResponse<FeedbackResponseDTO> response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(feedback.getId(), response.body().getId());
        assertEquals(dto.getContent(), response.body().getContent());
        assertEquals(dto.getConfidential(), response.body().getConfidential());

        // Update by owner
        dto.setContent("Alice makes the feedback private again");
        dto.setConfidential(true);
        dto.setUpdatedOn(LocalDateTime.now());

        request = HttpRequest.PUT("", dto).basicAuth(alice.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        response = client.toBlocking().exchange(request, FeedbackResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(dto.getId(), response.body().getId());
        assertEquals(dto.getContent(), response.body().getContent());
        assertEquals(dto.getConfidential(), response.body().getConfidential());
    }

    @Test
    public void testPutIdNotFound() {
        // TODO: test when the feedback id specified in the FeedbackUpdateDTO is invalid
    }

    @Test
    public void testPutPermissionDenied() {
        // TODO: test when the user trying to update a feedback is not admin and not the creator of the feedback
    }

    @Test
    public void testDeleteSucceedAdmin() {
        // TODO: admin should be able to delete a feedback
    }

    @Test
    public void testDeleteSucceedOwner() {
        // TODO: the creator of a feedback should be able to delete it
    }

    @Test
    public void testDeleteIdNotFound() {
        // TODO: test when there is no feedback with the given id to delete
    }

    @Test
    public void testDeletePermissionDenied() {
        // TODO: test when the current user is neither admin nor the creator of the feedback
    }

    private void assertEntityEqualsResponse(Feedback entity, FeedbackResponseDTO dto) {
        if (entity == null || dto == null) {
            assertEquals(entity, dto);
        } else {
            assertEquals(entity.getId(), dto.getId());
            assertEquals(entity.getContent(), dto.getContent());
            assertEquals(entity.getSentBy(), dto.getSentBy());
            assertEquals(entity.getSentTo(), dto.getSentTo());
            assertEquals(entity.getConfidential(), dto.getConfidential());
            assertEqualDateTime(entity.getCreatedOn(), dto.getCreatedOn());
            assertEqualDateTime(entity.getUpdatedOn(), dto.getUpdatedOn());
        }
    }

    private void assertEqualDateTime(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) {
            assertEquals(a, b);
        } else {
            assertEquals(a.getYear(), b.getYear());
            assertEquals(a.getMonth(), b.getMonth());
            assertEquals(a.getDayOfMonth(), b.getDayOfMonth());
            assertEquals(a.getHour(), b.getHour());
            assertEquals(a.getMinute(), b.getMinute());
            assertEquals(a.getSecond(), b.getSecond());
        }
    }
}
