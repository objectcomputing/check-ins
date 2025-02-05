package com.objectcomputing.checkins.services.pulseresponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.PulseResponseFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.util.Util;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.services.SlackSearchReplacement;

import io.micronaut.core.util.StringUtils;
import io.micronaut.core.type.Argument;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.Instant;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.slacksearch", value = StringUtils.TRUE)
class PulseResponseControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, PulseResponseFixture {

    @Inject
    @Client("/services/pulse-responses")
    protected HttpClient client;

    @Inject
    private CheckInsConfiguration configuration;

    @Inject
    private SlackSearchReplacement slackSearch;

    private Map<String, MemberProfile> hierarchy;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
        hierarchy = createHierarchy();
    }

    @Test
    void testCreateAPulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponseCreateDTO pulseResponseCreateDTO = createPulseResponseCreateDTO(memberProfile.getId());

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        PulseResponse pulseResponseResponse = response.body();

        Assertions.assertNotNull(pulseResponseResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(pulseResponseCreateDTO.getTeamMemberId(), pulseResponseResponse.getTeamMemberId());
        assertEquals(String.format("%s/%s", request.getPath(), pulseResponseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidPulseResponse() {
        PulseResponseCreateDTO pulseResponseCreateDTO = new PulseResponseCreateDTO();

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = Stream.of(
          errors.get(0).get("message").asText(),
          errors.get(1).get("message").asText()
        ).sorted().collect(Collectors.toList());

        assertEquals(2, errorList.size());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreatePulseResponseForNonExistingMember() {
        PulseResponseCreateDTO pulseResponseCreateDTO = createPulseResponseCreateDTO();

        HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exists", pulseResponseCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreatePulseResponseForSomeoneUnrelated() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponseCreateDTO pulseResponseCreateDTO = createPulseResponseCreateDTO(id(HIERARCHY_LEAD2));

        HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User %s does not have permission to create pulse response for user %s".formatted(memberProfile.getId(),  pulseResponseCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreatePulseResponseForSomeoneInHierarchy() {
        PulseResponseCreateDTO pulseResponseCreateDTO = createPulseResponseCreateDTO(id(HIERARCHY_LEAD2_SUB1_SUB1));

        HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(profile(HIERARCHY_LEAD2).getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        PulseResponse pulseResponseResponse = response.body();

        Assertions.assertNotNull(pulseResponseResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(pulseResponseCreateDTO.getTeamMemberId(), pulseResponseResponse.getTeamMemberId());
        assertEquals(String.format("%s/%s", request.getPath(), pulseResponseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateANullPulseResponse() {
        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [pulseResponse] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAPulseResponseForInvalidDate() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponseCreateDTO pulseResponseCreateDTO = createPulseResponseCreateDTO(memberProfile.getId(), LocalDate.of(1965, 11, 12));

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid date for pulseresponse submission date %s", pulseResponseCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testGetFindByTeamMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(pulseResponse), response.body());
    }

    @Test
    void testAdminWithPermissionCanReadAnyResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));
        assertEquals(Set.of(pulseResponse), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testCannotReadResponseForUnrelatedUsers() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().isEmpty(), "Expected empty set of responses");
    }

    @Test
    void testCanReadResponsesForReportingHierarchy() {
        MemberProfile ceo = profile(HIERARCHY_CEO);
        MemberProfile supervisor = profile(HIERARCHY_LEAD1);
        MemberProfile member = profile(HIERARCHY_LEAD1_SUB2);

        // when we make a response for the member
        PulseResponse pulseResponse = createADefaultPulseResponse(member);

        // then the supervisor can read the response
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(supervisor.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(pulseResponse), response.body());

        // and the CEO can read the response
        HttpRequest<?> ceoRequest = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(ceo.getWorkEmail(), MEMBER_ROLE);
        HttpResponse<Set<PulseResponse>> ceoResponse = client.toBlocking().exchange(ceoRequest, Argument.setOf(PulseResponse.class));
        assertEquals(HttpStatus.OK, ceoResponse.getStatus());
        assertEquals(Set.of(pulseResponse), ceoResponse.body());
    }

    @Test
    void testCannotReadResponsesForLowerInTheReportingHierarchy() {
        MemberProfile ceo = profile(HIERARCHY_CEO);
        MemberProfile supervisor = profile(HIERARCHY_LEAD1);
        MemberProfile member = profile(HIERARCHY_LEAD1_SUB2);

        // when we make a response for the supervisor
        PulseResponse pulseResponse = createADefaultPulseResponse(supervisor);

        // then the member cannot read the response
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().isEmpty(), "Expected empty set of responses");

        // but the CEO can read the response
        HttpRequest<?> ceoRequest = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(ceo.getWorkEmail(), MEMBER_ROLE);
        HttpResponse<Set<PulseResponse>> ceoResponse = client.toBlocking().exchange(ceoRequest, Argument.setOf(PulseResponse.class));
        assertEquals(HttpStatus.OK, ceoResponse.getStatus());
        assertEquals(Set.of(pulseResponse), ceoResponse.body());
    }

    @Test
    void testCannotReadResponsesForOutsideOfReportingHierarchy() {
        PulseResponse pulseResponse = createADefaultPulseResponse(profile(HIERARCHY_LEAD1_SUB2));
        var supervisor = profile(HIERARCHY_LEAD2);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(supervisor.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().isEmpty(), "Expected empty set of responses");
    }

    // Find By findBySubmissionDateBetween returns empty array - when no data exists
    @Test
    void testGetFindBySubmissionDateBetweenReturnsEmptyBody() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createADefaultPulseResponse(memberProfile);

        LocalDate testDateFrom = LocalDate.of(2019, 1, 1);
        LocalDate testDateTo = LocalDate.of(2019, 2, 1);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().isEmpty(), "Expected empty set of responses");
    }

    // Find By findBySubmissionDateBetween
    @Test
    void testGetFindByfindBySubmissionDateBetween() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);

        LocalDate testDateFrom = LocalDate.of(2019, 1, 1);
        LocalDate testDateTo = Util.MAX.toLocalDate();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(pulseResponse), response.body());
    }

    @Test
    void testAnonymousGetFindByfindBySubmissionDateBetween() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultAnonymousPulseResponse();

        LocalDate testDateFrom = LocalDate.of(2019, 1, 1);
        LocalDate testDateTo = Util.MAX.toLocalDate();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(pulseResponse), response.body());
    }

    @Test
    void testGetFindById() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", pulseResponse.getId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(pulseResponse), response.body());
    }

    @Test
    void testFindPulseResponseAllParams() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", pulseResponse.getTeamMemberId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(pulseResponse), response.body());
    }

    @Test
    void testPulseResponseDoesNotExist() {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().isEmpty(), "Expected empty set of responses");
    }

    @Test
    void testUpdatePulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(pulseResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), pulseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdatePulseResponseForSubordinateMember() {
        PulseResponse pulseResponse = createADefaultPulseResponse(profile(HIERARCHY_LEAD2_SUB1_SUB1));

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse).basicAuth(profile(HIERARCHY_LEAD2).getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(pulseResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), pulseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdatePulseResponseForSameMember() {
        PulseResponse pulseResponse = createADefaultPulseResponse(profile(HIERARCHY_LEAD2_SUB1_SUB1));

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse).basicAuth(profile(HIERARCHY_LEAD2_SUB1_SUB1).getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(pulseResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), pulseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdatePulseResponseForSupervisoryMember() {
        PulseResponse pulseResponse = createADefaultPulseResponse(profile(HIERARCHY_LEAD2));

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse).basicAuth(profile(HIERARCHY_LEAD2_SUB1_SUB1).getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("User %s does not have permission to update pulse response for user %s".formatted(id(HIERARCHY_LEAD2_SUB1_SUB1), id(HIERARCHY_LEAD2)), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingPulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);
        pulseResponse.setId(UUID.randomUUID());

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find pulseresponse record with id %s", pulseResponse.getId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdatePulseResponseForUnrelatedMember() {
        PulseResponse pulseResponse = createADefaultPulseResponse(profile(HIERARCHY_LEAD2));

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse).basicAuth(profile(HIERARCHY_LEAD1).getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("User %s does not have permission to update pulse response for user %s".formatted(id(HIERARCHY_LEAD1), id(HIERARCHY_LEAD2)), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNotExistingMemberPulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);
        pulseResponse.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", pulseResponse.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNotMemberPulseResponseWithoutId() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);
        pulseResponse.setId(null);

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("Unable to find pulseresponse record with id null", error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateUnAuthorized() {
        PulseResponse pulseResponse = new PulseResponse(1, 2, LocalDate.now(), UUID.randomUUID(), "internalfeeling", "externalfeeling");

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testUpdateANullPulseResponse() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [pulseResponse] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateInvalidDatePulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponse pulseResponse = createADefaultPulseResponse(memberProfile);
        pulseResponse.setInternalScore(1);
        pulseResponse.setExternalScore(2);
        pulseResponse.setSubmissionDate(LocalDate.of(1965, 12, 11));

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for pulseresponse submission date %s", pulseResponse.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateAPulseResponseFromSlack() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        slackSearch.users.put("SLACK_ID_HI", memberProfile.getWorkEmail());

        final String rawBody = "payload=%7B%22type%22%3A+%22view_submission%22%2C+%22user%22%3A+%7B%22id%22%3A+%22SLACK_ID_HI%22%7D%2C+%22view%22%3A+%7B%22id%22%3A+%22VNHU13V36%22%2C+%22type%22%3A+%22modal%22%2C+%22state%22%3A+%7B%22values%22%3A+%7B%22internalNumber%22%3A+%7B%22internalScore%22%3A+%7B%22selected_option%22%3A+%7B%22type%22%3A+%22radio_buttons%22%2C+%22value%22%3A+%224%22%7D%7D%7D%2C+%22internalText%22%3A+%7B%22internalFeelings%22%3A+%7B%22type%22%3A+%22plain_text_input%22%2C+%22value%22%3A+%22I+am+a+robot.%22%7D%7D%2C+%22externalNumber%22%3A+%7B%22externalScore%22%3A+%7B%22selected_option%22%3A+%7B%22type%22%3A+%22radio_buttons%22%2C+%22value%22%3A+%225%22%7D%7D%7D%2C+%22externalText%22%3A+%7B%22externalFeelings%22%3A+%7B%22type%22%3A+%22plain_text_input%22%2C+%22value%22%3A+%22You+are+a+robot.%22%7D%7D%7D%7D%7D%7D";

        long currentTime = Instant.now().getEpochSecond();
        String timestamp = String.valueOf(currentTime);

        final HttpRequest request = HttpRequest.POST("/external", rawBody)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("X-Slack-Signature", slackSignature(timestamp, rawBody))
        .header("X-Slack-Request-Timestamp", timestamp);

        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    private String slackSignature(String timestamp, String rawBody) {
        String baseString = "v0:" + timestamp + ":" + rawBody;
        String secret = configuration.getApplication()
                                     .getSlack().getSigningSecret();

        try {
            // Generate HMAC SHA-256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));

            // Convert hash to hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return "v0=" + hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static PulseResponseCreateDTO createPulseResponseCreateDTO() {
        return createPulseResponseCreateDTO(UUID.randomUUID());
    }

    private static PulseResponseCreateDTO createPulseResponseCreateDTO(UUID memberId) {
        return createPulseResponseCreateDTO(memberId, LocalDate.now());
    }

    private static PulseResponseCreateDTO createPulseResponseCreateDTO(UUID memberId, LocalDate submissionDate) {
        PulseResponseCreateDTO pulseResponseCreateDTO = new PulseResponseCreateDTO();
        pulseResponseCreateDTO.setInternalScore(1);
        pulseResponseCreateDTO.setExternalScore(2);
        pulseResponseCreateDTO.setSubmissionDate(submissionDate);
        pulseResponseCreateDTO.setTeamMemberId(memberId);
        pulseResponseCreateDTO.setInternalFeelings("internalfeelings");
        pulseResponseCreateDTO.setExternalFeelings("externalfeelings");
        return pulseResponseCreateDTO;
    }


    private MemberProfile profile(String key) {
        return hierarchy.get(key);
    }

    private UUID id(String key) {
        return profile(key).getId();
    }
}
