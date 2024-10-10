package com.objectcomputing.checkins.services.reviews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.ReviewAssignmentFixture;
import com.objectcomputing.checkins.services.fixture.ReviewPeriodFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.EmailHelper;
import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
class ReviewPeriodControllerTest
        extends TestContainersSuite
        implements ReviewAssignmentFixture, ReviewPeriodFixture, MemberProfileFixture, RoleFixture, FeedbackRequestFixture, FeedbackTemplateFixture {

    public static final Logger LOG = LoggerFactory.getLogger(ReviewPeriodControllerTest.class);

    @Inject
    @Client("/services/review-periods")
    private HttpClient client;

    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    private ReviewPeriodServicesImpl reviewPeriodServices;

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
        emailSender.reset();
    }

    @Test
    void testGETNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testGETFindByNameReturnsEmptyBody() {
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<ReviewPeriod>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewPeriod.class));

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETFindByValueName() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue(reviewPeriod.getName()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<ReviewPeriod>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewPeriod.class));

        assertEquals(Set.of(reviewPeriod), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETFindByValueStatus() {
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
    void testGETGetByIdHappyPath() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", reviewPeriod.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETGetByIdNotFound() {
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testPOSTCreateAReviewPeriod() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

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
    void testPOSTCreateAReviewPeriodForbiddenWithoutPermission() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ReviewPeriod.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testReviewPeriodCreateDTOSerialization() throws JsonProcessingException {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);

        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);
        reviewPeriodCreateDTO.setLaunchDate(launchDate);
        reviewPeriodCreateDTO.setSelfReviewCloseDate(selfReviewCloseDate);
        reviewPeriodCreateDTO.setCloseDate(closeDate);
        reviewPeriodCreateDTO.setPeriodStartDate(startDate);
        reviewPeriodCreateDTO.setPeriodEndDate(endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String expectedLaunchDateFormat = formatter.format(reviewPeriodCreateDTO.getLaunchDate());
        String expectedSelfReviewCloseDateFormat = formatter.format(reviewPeriodCreateDTO.getSelfReviewCloseDate());
        String expectedCloseDateFormat = formatter.format(reviewPeriodCreateDTO.getCloseDate());
        String expectedPeriodStartDateFormat = formatter.format(reviewPeriodCreateDTO.getPeriodStartDate());
        String expectedPeriodEndDateFormat = formatter.format(reviewPeriodCreateDTO.getPeriodEndDate());

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertNotNull(response);
        var actualJson = response.body();
        assertNotNull(actualJson);
        assertEquals(HttpStatus.CREATED, response.getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        String actualLaunchDateFormat = objectMapper.readTree(actualJson).get("launchDate").asText();
        assertEquals(expectedLaunchDateFormat, actualLaunchDateFormat);

        String actualSelfReviewCloseDateFormat = objectMapper.readTree(actualJson).get("selfReviewCloseDate").asText();
        assertEquals(expectedSelfReviewCloseDateFormat, actualSelfReviewCloseDateFormat);

        String actualCloseDateFormat = objectMapper.readTree(actualJson).get("closeDate").asText();
        assertEquals(expectedCloseDateFormat, actualCloseDateFormat);

        String actualPeriodStartDateFormat = objectMapper.readTree(actualJson).get("periodStartDate").asText();
        assertEquals(expectedPeriodStartDateFormat, actualPeriodStartDateFormat);

        String actualPeriodEndDateFormat = objectMapper.readTree(actualJson).get("periodEndDate").asText();
        assertEquals(expectedPeriodEndDateFormat, actualPeriodEndDateFormat);
    }

    @Test
    void testReviewPeriodCreateDTOSerializationForbiddenWithoutPermission() throws JsonProcessingException {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);
        reviewPeriodCreateDTO.setLaunchDate(LocalDateTime.now());
        reviewPeriodCreateDTO.setSelfReviewCloseDate(LocalDateTime.now());
        reviewPeriodCreateDTO.setCloseDate(LocalDateTime.now());
        reviewPeriodCreateDTO.setPeriodStartDate(LocalDateTime.now());
        reviewPeriodCreateDTO.setPeriodEndDate(LocalDateTime.now());

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, String.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPOSTCreateAReviewPeriodWithTimelines() {
        LocalDateTime launchDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime selfReviewCloseDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime closeDate = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime periodStartDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime periodEndDate = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.MILLIS);

        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);
        reviewPeriodCreateDTO.setLaunchDate(launchDate);
        reviewPeriodCreateDTO.setSelfReviewCloseDate(selfReviewCloseDate);
        reviewPeriodCreateDTO.setCloseDate(closeDate);
        reviewPeriodCreateDTO.setPeriodStartDate(periodStartDate);
        reviewPeriodCreateDTO.setPeriodEndDate(periodEndDate);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
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
        assertEquals(reviewPeriodCreateDTO.getPeriodStartDate(), body.getPeriodStartDate());
        assertEquals(reviewPeriodCreateDTO.getPeriodEndDate(), body.getPeriodEndDate());
    }

    @Test
    void testPOSTCreateAReviewPeriodWithTimelinesForbiddenWithoutPermission() {
        LocalDateTime launchDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime selfReviewCloseDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime closeDate = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime periodStartDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime periodEndDate = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.MILLIS);

        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);
        reviewPeriodCreateDTO.setLaunchDate(launchDate);
        reviewPeriodCreateDTO.setSelfReviewCloseDate(selfReviewCloseDate);
        reviewPeriodCreateDTO.setCloseDate(closeDate);
        reviewPeriodCreateDTO.setPeriodStartDate(periodStartDate);
        reviewPeriodCreateDTO.setPeriodEndDate(periodEndDate);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ReviewPeriod.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPOSTCreateAReviewPeriodAlreadyExists() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setReviewStatus(reviewPeriod.getReviewStatus());

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
    }

    @Test
    void testPOSTCreateAReviewPeriodAlreadyExistsForbiddenWithoutPermission() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setReviewStatus(reviewPeriod.getReviewStatus());

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPOSTCreateAReviewPeriodAlreadyExistsWhenClosed() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
    }

    @Test
    void testPOSTCreateAReviewPeriodAlreadyExistsWhenClosedForbiddenWithoutPermission() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPOSTCreateANullReviewPeriod() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testPOSTCreateANullReviewPeriodForbiddenWithoutPermission() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testUpdateReviewPeriodAsAdmin() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        reviewPeriod.setReviewStatus(ReviewStatus.OPEN);

        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    private static Stream<Arguments> validStatusTransitions() {
        return Stream.of(
                Arguments.of(ReviewStatus.PLANNING, ReviewStatus.PLANNING),
                Arguments.of(ReviewStatus.PLANNING, ReviewStatus.AWAITING_APPROVAL),
                Arguments.of(ReviewStatus.AWAITING_APPROVAL, ReviewStatus.AWAITING_APPROVAL),
                Arguments.of(ReviewStatus.AWAITING_APPROVAL, ReviewStatus.OPEN),
                Arguments.of(ReviewStatus.OPEN, ReviewStatus.OPEN),
                Arguments.of(ReviewStatus.OPEN, ReviewStatus.CLOSED),
                Arguments.of(ReviewStatus.CLOSED, ReviewStatus.CLOSED),
                Arguments.of(ReviewStatus.CLOSED, ReviewStatus.OPEN),
                Arguments.of(ReviewStatus.UNKNOWN, ReviewStatus.UNKNOWN),
                Arguments.of(ReviewStatus.UNKNOWN, ReviewStatus.PLANNING)
        );
    }

    @ParameterizedTest
    @MethodSource("validStatusTransitions")
    void testUpdateReviewPeriodAsAdminWithValidStatusTransitions(ReviewStatus oldStatus, ReviewStatus newStatus) {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod(oldStatus);
        reviewPeriod.setReviewStatus(newStatus);

        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    private static Stream<Arguments> invalidStatusTransitions() {
        var validTransitions = validStatusTransitions().toList();
        var allPermutations = new ArrayList<Arguments>();
        for (ReviewStatus from : ReviewStatus.values()) {
            for (ReviewStatus to : ReviewStatus.values()) {
                if (validTransitions.stream().noneMatch(a -> a.get()[0] == from && a.get()[1] == to)) {
                    allPermutations.add(Arguments.of(from, to));
                }
            }
        }
        return allPermutations.stream();
    }

    @ParameterizedTest
    @MethodSource("invalidStatusTransitions")
    void testUpdateReviewPeriodAsAdminWithInvalidStatusTransitions(ReviewStatus oldStatus, ReviewStatus newStatus) {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod(oldStatus);
        reviewPeriod.setReviewStatus(newStatus);

        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ReviewPeriod.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Invalid status transition from %s to %s".formatted(oldStatus, newStatus), responseException.getMessage());
    }

    @Test
    void testPUTReviewPeriodAwaitingApproval() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod(ReviewStatus.PLANNING);
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
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null",
                                  "Review Assignments Awaiting Approval",
                                  "Click <a href=\"https://checkins.objectcomputing.com/feedback/reviews?period=" + reviewPeriod.getId() + "\">here</a> to review and approve reviewer assignments in the Check-Ins app.",
                                  supervisor.getWorkEmail(),
                                  emailSender.events.getFirst()
        );
    }

    @Test
    void testPUTReviewPeriodOpen() {
        MemberProfile supervisor = createADefaultSupervisor();
        FeedbackTemplate template = saveReviewFeedbackTemplate(supervisor.getId());
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod(ReviewStatus.AWAITING_APPROVAL, template.getId());
        MemberProfile member = createAProfileWithSupervisorAndPDL(supervisor, supervisor);

        createAReviewAssignmentBetweenMembers(member, supervisor, reviewPeriod, false);

        reviewPeriod.setReviewStatus(ReviewStatus.OPEN);
        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(supervisor.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(reviewPeriod, response.body());

        // Check for the feedback request.  There should only be one because
        // there was a review template, but no self-review template.
        List<FeedbackRequest> requests = getFeedbackRequests(supervisor);
        assertEquals(1, requests.size());

        FeedbackRequest feedbackRequest = requests.get(0);
        assertEquals(member.getId(), feedbackRequest.getRequesteeId());
        assertEquals(reviewPeriod.getId(), feedbackRequest.getReviewPeriodId());

        assertEquals(2, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null",
                                  "It's time for performance reviews!",
                                  "Help us make this a valuable experience for everyone!",
                                  member.getWorkEmail() + "," + supervisor.getWorkEmail(),
                                  emailSender.events.get(1)
        );
    }

    @Test
    void testReviewPeriodSerialization() throws JsonProcessingException {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

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

        String expectedPeriodStartDateFormat = objectMapper.readTree(expectedJson).get("periodStartDate").asText();
        String actualPeriodStartDateFormat = objectMapper.readTree(actualJson).get("periodStartDate").asText();
        assertEquals(expectedPeriodStartDateFormat, actualPeriodStartDateFormat);

        String expectedPeriodEndDateFormat = objectMapper.readTree(expectedJson).get("periodEndDate").asText();
        String actualPeriodEndDateFormat = objectMapper.readTree(actualJson).get("periodEndDate").asText();
        assertEquals(expectedPeriodEndDateFormat, actualPeriodEndDateFormat);
    }

    @Test
    void testPUTUpdateReviewPeriodNonAdmin() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        final HttpRequest<ReviewPeriod> request = HttpRequest.PUT("/", reviewPeriod)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPUTUpdateNonexistentReviewPeriod() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

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
    void testPUTUpdateNullReviewPeriod() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deleteReviewPeriodAsAdmin() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        assertEquals(1, getReviewPeriodRepository().findAll().size());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", reviewPeriod.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        assertTrue(getReviewPeriodRepository().findAll().isEmpty(), "Review period should be deleted");
    }

    @Test
    void deleteReviewPeriodNotAsAdmin() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        assertEquals(1, getReviewPeriodRepository().findAll().size());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", reviewPeriod.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(1, getReviewPeriodRepository().findAll().size());
    }

    @Test
    void deleteReviewPeriodWithFeedbackRequests() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignAdminRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        saveFeedbackRequest(pdlMemberProfile, requestee, recipient, reviewPeriod);

        assertEquals(1, getReviewPeriodRepository().findAll().size());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", reviewPeriod.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(1, getReviewPeriodRepository().findAll().size());
        assertEquals(
                "Review Period %s has associated feedback requests and cannot be deleted".formatted(reviewPeriod.getId()),
                responseException.getMessage()
        );
    }

    @Test
    void testOpenAReviewPeriodWithBadLaunchTime() {
        LocalDateTime launchDate = LocalDateTime.now().minusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        ReviewPeriod period = getReviewPeriodRepository().save(
            new ReviewPeriod("Good Times, Bad Times",
                             ReviewStatus.AWAITING_APPROVAL, null, null,
                             launchDate, selfReviewCloseDate, closeDate,
                             startDate, endDate));

        period.setReviewStatus(ReviewStatus.OPEN);
        assertThrows(
            BadArgException.class,
            () -> reviewPeriodServices.update(period),
            "Expected ReviewPeriodServices.update() to throw, but it didn't"
        );
    }

    @Test
    void testCreateAReviewPeriodWithBadSelfReviewCloseDate() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.minusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        BadArgException exception = assertThrows(
            BadArgException.class,
            () -> reviewPeriodServices.save(
                new ReviewPeriod("Good Times, Bad Times",
                                 ReviewStatus.AWAITING_APPROVAL, null, null,
                                 launchDate, selfReviewCloseDate, closeDate,
                                 startDate, endDate)),
           "Expected ReviewPeriodServices.save() to throw, but it didn't"
        );
        assertTrue(exception.getMessage()
                            .contains("self-review close date must be after"));
    }

    @Test
    void testCreateAReviewPeriodWithBadCloseDate() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = launchDate.minusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        BadArgException exception = assertThrows(
            BadArgException.class,
            () -> reviewPeriodServices.save(
                new ReviewPeriod("Good Times, Bad Times",
                                 ReviewStatus.AWAITING_APPROVAL, null, null,
                                 launchDate, selfReviewCloseDate, closeDate,
                                 startDate, endDate)),
           "Expected ReviewPeriodServices.save() to throw, but it didn't"
        );
        assertTrue(exception.getMessage()
                            .contains("close date must be after"));
    }

    @Test
    void testCreateAReviewPeriodWithBadStartDate() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate;
        LocalDateTime endDate = closeDate.minusDays(1);
        BadArgException exception = assertThrows(
            BadArgException.class,
            () -> reviewPeriodServices.save(
                new ReviewPeriod("Good Times, Bad Times",
                                 ReviewStatus.AWAITING_APPROVAL, null, null,
                                 launchDate, selfReviewCloseDate, closeDate,
                                 startDate, endDate)),
           "Expected ReviewPeriodServices.save() to throw, but it didn't"
        );
        assertTrue(exception.getMessage()
                            .contains("start date must be before"));
    }

    @Test
    void testCreateAReviewPeriodWithBadEndDate1() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = startDate;
        BadArgException exception = assertThrows(
            BadArgException.class,
            () -> reviewPeriodServices.save(
                new ReviewPeriod("Good Times, Bad Times",
                                 ReviewStatus.AWAITING_APPROVAL, null, null,
                                 launchDate, selfReviewCloseDate, closeDate,
                                 startDate, endDate)),
           "Expected ReviewPeriodServices.save() to throw, but it didn't"
        );
        assertTrue(exception.getMessage()
                            .contains("end date must be after"));
    }

    @Test
    void testCreateAReviewPeriodWithBadEndDate2() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.plusDays(1);
        BadArgException exception = assertThrows(
            BadArgException.class,
            () -> reviewPeriodServices.save(
                new ReviewPeriod("Good Times, Bad Times",
                                 ReviewStatus.AWAITING_APPROVAL, null, null,
                                 launchDate, selfReviewCloseDate, closeDate,
                                 startDate, endDate)),
           "Expected ReviewPeriodServices.save() to throw, but it didn't"
        );
        assertTrue(exception.getMessage()
                            .contains("end date must be on or before"));
    }
}
