package com.objectcomputing.checkins.services.reviews;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewAssignmentControllerTest extends TestContainersSuite implements ReviewAssignmentFixture, ReviewPeriodFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/review-assignments")
    private HttpClient client;

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void createRolesAndPermissions() {

        createAndAssignRoles();
    }


    @Test
    public void testPOSTCreateAReviewAssignment() {
        ReviewAssignmentDTO reviewAssignmentDTO = new ReviewAssignmentDTO();
        reviewAssignmentDTO.setRevieweeId(UUID.randomUUID());
        reviewAssignmentDTO.setReviewerId(UUID.randomUUID());
        reviewAssignmentDTO.setReviewPeriodId(UUID.randomUUID());
        reviewAssignmentDTO.setApproved(false);

        final HttpRequest<ReviewAssignmentDTO> request = HttpRequest.
            POST("/", reviewAssignmentDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<ReviewAssignment> response = client.toBlocking().exchange(request, ReviewAssignment.class);

        assertNotNull(response);
        var body = response.body();
        assertNotNull(body);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(reviewAssignmentDTO.getRevieweeId(), body.getRevieweeId());
        assertEquals(reviewAssignmentDTO.getReviewerId(), body.getReviewerId());
        assertEquals(reviewAssignmentDTO.getReviewPeriodId(), body.getReviewPeriodId());
        assertEquals(false, body.getApproved());
        assertEquals(String.format("%s/%s", request.getPath(), body.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testGETGetByIdHappyPath() {
        ReviewAssignment reviewAssignment = createADefaultReviewAssignment();

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/%s", reviewAssignment.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<ReviewAssignment> response = client.toBlocking().exchange(request, ReviewAssignment.class);

        assertEquals(reviewAssignment, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindAssignmentsByPeriodIdDefaultAssignments() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile supervisor = createADefaultSupervisor();
        MemberProfile anotherSupervisor = createAnotherSupervisor();

        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);

        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);

        MemberProfile memberOne = createAProfileWithSupervisorAndPDL(supervisor, pdlMemberProfile);
        MemberProfile memberTwo = createAnotherProfileWithSupervisorAndPDL(supervisor, pdlMemberProfileTwo);
        MemberProfile memberThree = createYetAnotherProfileWithSupervisorAndPDL(anotherSupervisor, pdlMemberProfileTwo);

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/period/%s", reviewPeriod.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<Set<ReviewAssignment>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewAssignment.class));

        assertNotNull(response.body());
        assertEquals(8, Objects.requireNonNull(response.body()).size());
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(memberOne.getId())));
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(memberTwo.getId())));
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(memberThree.getId())));
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    public void testGETFindAssignmentsByPeriodIdNoReviewer() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile supervisor = createADefaultSupervisor();
        MemberProfile member = createAProfileWithSupervisorAndPDL(supervisor, supervisor);

        ReviewAssignment reviewAssignment = createAReviewAssignmentBetweenMembers(member, supervisor, reviewPeriod, false);

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/period/%s", reviewPeriod.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<Set<ReviewAssignment>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewAssignment.class));

        assertNotNull(response.body());
        assertEquals(1, Objects.requireNonNull(response.body()).size());
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(member.getId())));
        assertTrue(response.body().stream().anyMatch(ra -> ra.getId().equals(reviewAssignment.getId())));
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    public void testGETFindAssignmentsByPeriodIdWithReviewer() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile supervisor = createADefaultSupervisor();
        MemberProfile member = createAProfileWithSupervisorAndPDL(supervisor, supervisor);

        ReviewAssignment reviewAssignment = createAReviewAssignmentBetweenMembers(member, supervisor, reviewPeriod, false);

        //Non-matching review assignments for control group
        createADefaultReviewAssignment();
        createADefaultReviewAssignment();

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/period/%s?%s", reviewPeriod.getId(), supervisor.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<Set<ReviewAssignment>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewAssignment.class));

        assertNotNull(response.body());
        assertEquals(1, Objects.requireNonNull(response.body()).size());
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(member.getId())));
        assertTrue(response.body().stream().anyMatch(ra -> ra.getId().equals(reviewAssignment.getId())));
        assertEquals(HttpStatus.OK, response.getStatus());
    }


}