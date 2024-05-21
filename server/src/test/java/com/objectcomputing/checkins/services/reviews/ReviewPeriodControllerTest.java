package com.objectcomputing.checkins.services.reviews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.ReviewAssignmentFixture;
import com.objectcomputing.checkins.services.fixture.ReviewPeriodFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReviewPeriodControllerTest extends TestContainersSuite implements ReviewAssignmentFixture, ReviewPeriodFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/review-periods")
    private HttpClient client;

    @Mock
    private final EmailSender emailSender = mock(EmailSender.class);

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGETFindByNameReturnsEmptyBody() {
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<ReviewPeriod>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewPeriod.class));

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByValueName() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue(reviewPeriod.getName()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<ReviewPeriod>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewPeriod.class));

        assertEquals(Set.of(reviewPeriod), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByValueStatus() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriod closedReviewPeriod = createAClosedReviewPeriod();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?reviewStatus=%s", encodeValue(String.valueOf(reviewPeriod.getReviewStatus())))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<ReviewPeriod>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewPeriod.class));

        assertNotNull(closedReviewPeriod);
        assertEquals(Set.of(reviewPeriod), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETGetByIdHappyPath() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", reviewPeriod.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETGetByIdNotFound() {
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateAReviewPeriod() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertNotNull(response);
        var body = response.body();
        assertNotNull(body);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(reviewPeriodCreateDTO.getName(), body.getName());
        assertEquals(reviewPeriodCreateDTO.getReviewStatus(), body.getReviewStatus());
        assertEquals(String.format("%s/%s", request.getPath(), body.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testReviewPeriodCreateDTOSerialization() throws JsonProcessingException {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);
        reviewPeriodCreateDTO.setLaunchDate(LocalDateTime.now());
        reviewPeriodCreateDTO.setSelfReviewCloseDate(LocalDateTime.now());
        reviewPeriodCreateDTO.setCloseDate(LocalDateTime.now());

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertNotNull(response);
        var actualJson = response.body();
        assertNotNull(actualJson);
        assertEquals(HttpStatus.CREATED, response.getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(reviewPeriodCreateDTO);

        String expectedLaunchDateFormat = objectMapper.readTree(expectedJson).get("launchDate").asText();
        String actualLaunchDateFormat = objectMapper.readTree(actualJson).get("launchDate").asText();
        assertEquals(expectedLaunchDateFormat, actualLaunchDateFormat);

        String expectedSelfReviewCloseDateFormat = objectMapper.readTree(expectedJson).get("selfReviewCloseDate").asText();
        String actualSelfReviewCloseDateFormat = objectMapper.readTree(actualJson).get("selfReviewCloseDate").asText();
        assertEquals(expectedSelfReviewCloseDateFormat, actualSelfReviewCloseDateFormat);

        String expectedCloseDateFormat = objectMapper.readTree(expectedJson).get("closeDate").asText();
        String actualCloseDateFormat = objectMapper.readTree(actualJson).get("closeDate").asText();
        assertEquals(expectedCloseDateFormat, actualCloseDateFormat);
    }

    @Test
    public void testPOSTCreateAReviewPeriodWithTimelines() {
        LocalDateTime launchDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime selfReviewCloseDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime closeDate = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS);

        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);
        reviewPeriodCreateDTO.setLaunchDate(launchDate);
        reviewPeriodCreateDTO.setSelfReviewCloseDate(selfReviewCloseDate);
        reviewPeriodCreateDTO.setCloseDate(closeDate);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertNotNull(response);
        var body = response.body();
        assertNotNull(body);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(reviewPeriodCreateDTO.getName(), body.getName());
        assertEquals(reviewPeriodCreateDTO.getReviewStatus(), body.getReviewStatus());
        assertEquals(String.format("%s/%s", request.getPath(), body.getId()), response.getHeaders().get("location"));
        assertEquals(reviewPeriodCreateDTO.getLaunchDate(), body.getLaunchDate());
        assertEquals(reviewPeriodCreateDTO.getSelfReviewCloseDate(), body.getSelfReviewCloseDate());
        assertEquals(reviewPeriodCreateDTO.getCloseDate(), body.getCloseDate());
    }

    @Test
    public void testPOSTCreateAReviewPeriodAlreadyExists() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setReviewStatus(reviewPeriod.getReviewStatus());

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateAReviewPeriodAlreadyExistsWhenClosed() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateANullReviewPeriod() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateReviewPeriodAsAdmin() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        reviewPeriod.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testPUTReviewPeriodAwaitingApproval() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile supervisor = createADefaultSupervisor();
        MemberProfile member = createAProfileWithSupervisorAndPDL(supervisor, supervisor);

        createAReviewAssignmentBetweenMembers(member, supervisor, reviewPeriod, false);

        reviewPeriod.setReviewStatus(ReviewStatus.AWAITING_APPROVAL);
        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(supervisor.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        // expect email has been sent
        verify(emailSender).sendEmail(null, null,
                "Review Assignments Awaiting Approval",
                "<h3>Review Assignments for Review Period '" + reviewPeriod.getName() + "' are ready for your approval.</h3>" +
                "<a href=\"https://checkins.objectcomputing.com/feedback/reviews?period=" + reviewPeriod.getId() + "\">Click here</a> to review and approve reviewer assignments in the Check-Ins app.",
                supervisor.getWorkEmail()
        );
    }

    @Test
    void testReviewPeriodSerialization() throws JsonProcessingException {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        reviewPeriod.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        String actualJson = response.body();
        assertEquals(HttpStatus.OK, response.getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(reviewPeriod);

        String expectedLaunchDateFormat = objectMapper.readTree(expectedJson).get("launchDate").asText();
        String actualLaunchDateFormat = objectMapper.readTree(actualJson).get("launchDate").asText();
        assertEquals(expectedLaunchDateFormat, actualLaunchDateFormat);

        String expectedSelfReviewCloseDateFormat = objectMapper.readTree(expectedJson).get("selfReviewCloseDate").asText();
        String actualSelfReviewCloseDateFormat = objectMapper.readTree(actualJson).get("selfReviewCloseDate").asText();
        assertEquals(expectedSelfReviewCloseDateFormat, actualSelfReviewCloseDateFormat);

        String expectedCloseDateFormat = objectMapper.readTree(expectedJson).get("closeDate").asText();
        String actualCloseDateFormat = objectMapper.readTree(actualJson).get("closeDate").asText();
        assertEquals(expectedCloseDateFormat, actualCloseDateFormat);
    }

    @Test
    public void testPUTUpdateReviewPeriodNonAdmin() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        final HttpRequest<ReviewPeriod> request = HttpRequest.PUT("/", reviewPeriod)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateNonexistentReviewPeriod() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                PUT("/", reviewPeriodCreateDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateNullReviewPeriod() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deleteReviewPeriodAsAdmin() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", reviewPeriod.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteReviewPeriodNotAsAdmin() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", reviewPeriod.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}