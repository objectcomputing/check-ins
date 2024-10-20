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
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

class ReviewAssignmentControllerTest extends TestContainersSuite implements ReviewAssignmentFixture, ReviewPeriodFixture, MemberProfileFixture, RoleFixture {

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
    void testPOSTCreateAReviewAssignment() {
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
    void testPOSTCreateMultipleReviewAssignments() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        ReviewAssignmentDTO reviewAssignmentDTO = new ReviewAssignmentDTO();
        reviewAssignmentDTO.setRevieweeId(UUID.randomUUID());
        reviewAssignmentDTO.setReviewerId(UUID.randomUUID());
        reviewAssignmentDTO.setReviewPeriodId(reviewPeriod.getId());
        reviewAssignmentDTO.setApproved(false);

        ReviewAssignmentDTO reviewAssignmentDTO2 = new ReviewAssignmentDTO();
        reviewAssignmentDTO2.setRevieweeId(UUID.randomUUID());
        reviewAssignmentDTO2.setReviewerId(UUID.randomUUID());
        reviewAssignmentDTO2.setReviewPeriodId(reviewPeriod.getId());
        reviewAssignmentDTO2.setApproved(false);

        List<ReviewAssignmentDTO> reviewAssignments = List.of(reviewAssignmentDTO, reviewAssignmentDTO2);

        final HttpRequest<List<ReviewAssignmentDTO>> request = HttpRequest.
            POST(String.format("/%s", reviewPeriod.getId()), reviewAssignments).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<List<ReviewAssignment>> response = client.toBlocking().exchange(request, Argument.listOf(ReviewAssignment.class));

        assertNotNull(response);
        var body = response.body();
        assertNotNull(body);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(2, Objects.requireNonNull(response.body()).size());
        assertTrue(body.stream().anyMatch(ra -> ra.getRevieweeId().equals(reviewAssignmentDTO.getRevieweeId())));
        assertTrue(body.stream().anyMatch(ra -> ra.getRevieweeId().equals(reviewAssignmentDTO2.getRevieweeId())));
        assertTrue(body.stream().allMatch(ra -> ra.getReviewPeriodId().equals(reviewPeriod.getId())));
        assertTrue(body.stream().allMatch(ra -> {
            assert ra.getApproved() != null;
            return ra.getApproved().equals(false);
        }));


        final HttpRequest<Object> request2 = HttpRequest.
            GET(String.format("/period/%s", reviewPeriod.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<ReviewAssignment>> response2 = client.toBlocking().exchange(request2, Argument.listOf(ReviewAssignment.class));

        assertNotNull(response2.body());
        body = response2.body();
        assertEquals(2, Objects.requireNonNull(response2.body()).size());
        assertTrue(body.stream().anyMatch(ra -> ra.getRevieweeId().equals(reviewAssignmentDTO.getRevieweeId())));
        assertTrue(body.stream().anyMatch(ra -> ra.getRevieweeId().equals(reviewAssignmentDTO2.getRevieweeId())));
        assertEquals(HttpStatus.OK, response2.getStatus());
    }

    @Test
    void testGETGetByIdHappyPath() {
        ReviewAssignment reviewAssignment = createADefaultReviewAssignment();

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/%s", reviewAssignment.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<ReviewAssignment> response = client.toBlocking().exchange(request, ReviewAssignment.class);

        assertEquals(reviewAssignment, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETFindAssignmentsByPeriodIdDefaultAssignments() {
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
        assertEquals(0, Objects.requireNonNull(response.body()).size());
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(memberOne.getId())));
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(memberTwo.getId())));
        assertTrue(response.body().stream().anyMatch(ra -> ra.getRevieweeId().equals(memberThree.getId())));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETFindAssignmentsByPeriodIdWithoutPermissions() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile supervisor = createADefaultSupervisor();
        MemberProfile anotherSupervisor = createAnotherSupervisor();

        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);

        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);

        createAProfileWithSupervisorAndPDL(supervisor, pdlMemberProfile);
        createAnotherProfileWithSupervisorAndPDL(supervisor, pdlMemberProfileTwo);
        createYetAnotherProfileWithSupervisorAndPDL(anotherSupervisor, pdlMemberProfileTwo);

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/period/%s", reviewPeriod.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }


    @Test
    void testGETFindAssignmentsByPeriodIdNoReviewer() {
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
    void testGETFindAssignmentsByPeriodIdWithReviewer() {
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

    @Test
    void testPUTUpdateReviewAssignmentWithoutPermissions() {
        ReviewAssignment reviewAssignment = createADefaultReviewAssignment();

        final HttpRequest<ReviewAssignment> request = HttpRequest.PUT("/", reviewAssignment)
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPUTUpdateNonexistentReviewAssignment() {
        ReviewAssignment reviewAssignment = new ReviewAssignment();
        reviewAssignment.setId(UUID.randomUUID());
        reviewAssignment.setRevieweeId(UUID.randomUUID());
        reviewAssignment.setReviewerId(UUID.randomUUID());

        final HttpRequest<ReviewAssignment> request = HttpRequest.
            PUT("/", reviewAssignment).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testPUTUpdateNullReviewAssignment() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deleteReviewAssignment() {
        ReviewAssignment reviewAssignment = createADefaultReviewAssignment();

        final HttpRequest<Object> request = HttpRequest.
            DELETE(String.format("/%s", reviewAssignment.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<ReviewAssignment> response = client.toBlocking().exchange(request, ReviewAssignment.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteReviewAssignmentWithoutPermissions() {
        ReviewAssignment reviewAssignment = createADefaultReviewAssignment();

        final HttpRequest<Object> request = HttpRequest.
            DELETE(String.format("/%s", reviewAssignment.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}