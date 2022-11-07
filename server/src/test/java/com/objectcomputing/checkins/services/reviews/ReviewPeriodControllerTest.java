package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReviewPeriodControllerTest extends TestContainersSuite implements ReviewPeriodFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/review-periods")
    private HttpClient client;

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

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
    public void testGETFindByValueOpen() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriod closedReviewPeriod = createAClosedReviewPeriod();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?open=%s", encodeValue(String.valueOf(reviewPeriod.isOpen())))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<ReviewPeriod>> response = client.toBlocking().exchange(request, Argument.setOf(ReviewPeriod.class));

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
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateAReviewPeriod() {
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName("reincarnation");
        reviewPeriodCreateDTO.setOpen(true);

        final HttpRequest<ReviewPeriodCreateDTO> request = HttpRequest.
                POST("/", reviewPeriodCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(reviewPeriodCreateDTO.getName(), response.body().getName());
        assertEquals(reviewPeriodCreateDTO.isOpen(), response.body().isOpen());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testPOSTCreateAReviewPeriodAlreadyExists() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        ReviewPeriodCreateDTO reviewPeriodCreateDTO = new ReviewPeriodCreateDTO();
        reviewPeriodCreateDTO.setName(reviewPeriod.getName());
        reviewPeriodCreateDTO.setOpen(reviewPeriod.isOpen());

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
        reviewPeriodCreateDTO.setOpen(false);

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
        reviewPeriod.setOpen(false);

        final HttpRequest<ReviewPeriod> request = HttpRequest.
                PUT("/", reviewPeriod).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<ReviewPeriod> response = client.toBlocking().exchange(request, ReviewPeriod.class);

        assertEquals(reviewPeriod, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
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
        reviewPeriodCreateDTO.setOpen(true);

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