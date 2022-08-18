package com.objectcomputing.checkins.services.referraltype;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.ReferralTypeFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.Resource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.referraltype.ReferralTypeTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReferralTypeControllerTest extends TestContainersSuite implements MemberProfileFixture, ReferralTypeFixture, RoleFixture {

    private static final Logger LOG = LoggerFactory.getLogger(ReferralTypeControllerTest.class);

    @Inject
    @Client("/services/referral-type")
    private HttpClient client;

    //TODO: Use Util.MAX instead of defining variable
    /*
     * LocalDate.Max cannot be used for end-to-end tests
     * LocalDate.Max year = 999999999
     * POSTGRES supported date range = 4713 BC - 5874897 AD
     */

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETAllReferralTypes() {
        createADefaultReferralType();
        createSecondDefaultReferralType();

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<ReferralTypeDTO>> response = client.toBlocking().exchange(request, Argument.listOf(ReferralTypeDTO.class));
        final List<ReferralTypeDTO> results = response.body();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, results.size());
    }

    @Test
    public void testGETGetById() {
        ReferralType referralType = createADefaultReferralType();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", referralType.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<ReferralTypeDTO> response = client.toBlocking().exchange(request, ReferralTypeDTO.class);

        assertReferralTypesEqual(referralType, response.body());
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
    public void testPOSTCreateReferralType() {

        ReferralTypeDTO dto = mkUpdateReferralTypeDTO();

        final HttpRequest<?> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<ReferralType> response = client.toBlocking().exchange(request, ReferralType.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getDiscoveredOpportunity(), response.body().getDiscoveredOpportunity());
        assertEquals(dto.getReferralTypeOther(), response.body().getReferralTypeOther());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPOSTCreateANullReferralType() {

        ReferralTypeDTO dto = new ReferralTypeDTO();

        final HttpRequest<ReferralTypeDTO> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateReferralType() {
        ReferralType firstReferralType = createADefaultReferralType();
        ReferralTypeDTO referralTypeUpdateDTO = ReferralTypeTestUtil.toDto(firstReferralType);

        referralTypeUpdateDTO.setReferredBy("Syd");

        final HttpRequest<ReferralTypeDTO> request = HttpRequest.PUT("/", referralTypeUpdateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<ReferralTypeDTO> response = client.toBlocking().exchange(request, ReferralTypeDTO.class);

        assertEquals(referralTypeUpdateDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), referralTypeUpdateDTO.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNonexistentReferralType() {

        ReferralTypeDTO dto = new ReferralTypeDTO();
        dto.setReferredBy("Syd");
        dto.setReferrerEmail("syd@gmail.com");

        final HttpRequest<ReferralTypeDTO> request = HttpRequest.
                PUT("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateNullReferralType() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPostWithNullPosition() {
        ReferralTypeCreateDTO requestBody = mkCreateReferralTypeDTO();
        requestBody.setReferredBy("Syd");

        final HttpRequest<ReferralTypeCreateDTO> request = HttpRequest.POST("", requestBody)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void testPutValidationFailures() {

        final HttpRequest<ReferralTypeDTO> request = HttpRequest.PUT("", new ReferralTypeDTO())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(3, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPutUpdateForEmptyInput() {
        ReferralTypeDTO testReferralType = mkUpdateReferralTypeDTO();
        testReferralType.setId(null);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testReferralType)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
        LOG.info(thrown.getResponse().getBody(JsonNode.class).toString());
        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        assertEquals("referralType.id: must not be null", error.asText());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testDeleteReferralType() {

        ReferralType referralType = createADefaultReferralType();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(referralType.getId().toString())
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is Deleted and not Terminated
        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", referralType.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No new employee profile for id " + referralType.getId(), error);
    }

}