package com.objectcomputing.checkins.services.employmentpreferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EmploymentPreferencesFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailabilityTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmploymentDesiredAvailabilityControllerTest extends TestContainersSuite implements MemberProfileFixture, EmploymentPreferencesFixture, RoleFixture {

    private static final Logger LOG = LoggerFactory.getLogger(EmploymentDesiredAvailabilityControllerTest.class);

    @Inject
    @Client("/services/employment-desired-availability")
    private HttpClient client;

    //TODO: Use Util.MAX instead of defining variable
    /*
     * LocalDate.Max cannot be used for end-to-end tests
     * LocalDate.Max year = 999999999
     * POSTGRES supported date range = 4713 BC - 5874897 AD
     */
    private final LocalDate maxDate = LocalDate.of(2099, 12, 31);

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETALLEmploymentPreferences() {
        createADefaultEmploymentPreferences();
        createSecondDefaultEmploymentPreferences();

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<EmploymentDesiredAvailabilityDTO>> response = client.toBlocking().exchange(request, Argument.listOf(EmploymentDesiredAvailabilityDTO.class));
        final List<EmploymentDesiredAvailabilityDTO> results = response.body();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, results.size());
    }

    @Test
    public void testGETGetById() {
        EmploymentDesiredAvailability employmentDesiredAvailability = createADefaultEmploymentPreferences();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", employmentDesiredAvailability.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<EmploymentDesiredAvailabilityDTO> response = client.toBlocking().exchange(request, EmploymentDesiredAvailabilityDTO.class);

        assertPreferencesEqual(employmentDesiredAvailability, response.body());
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
    public void testPOSTCreateEmploymentPreferences() {

        EmploymentDesiredAvailabilityDTO dto = mkUpdateEmploymentDesiredAvailabilityDTO();

        final HttpRequest<?> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<EmploymentDesiredAvailability> response = client.toBlocking().exchange(request, EmploymentDesiredAvailability.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getDesiredPosition(), response.body().getDesiredPosition());
        assertEquals(dto.getDesiredSalary(), response.body().getDesiredSalary());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPOSTCreateANullEmploymentPreferences() {

        EmploymentDesiredAvailabilityDTO dto = new EmploymentDesiredAvailabilityDTO();

        final HttpRequest<EmploymentDesiredAvailabilityDTO> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateEmploymentPreferences() {
        EmploymentDesiredAvailability firstProfile= createADefaultEmploymentPreferences();
        EmploymentDesiredAvailabilityDTO profileUpdateDTO = toDto(firstProfile);

        profileUpdateDTO.setDesiredSalary("10");

        final HttpRequest<EmploymentDesiredAvailabilityDTO> request = HttpRequest.PUT("/", profileUpdateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<EmploymentDesiredAvailabilityDTO> response = client.toBlocking().exchange(request, EmploymentDesiredAvailabilityDTO.class);

        assertEquals(profileUpdateDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), profileUpdateDTO.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNonexistentEmployeePreferences() {

        EmploymentDesiredAvailabilityDTO dto = new EmploymentDesiredAvailabilityDTO();
        dto.setDesiredSalary("10");
        dto.setDesiredPosition("Software Engineer");

        final HttpRequest<EmploymentDesiredAvailabilityDTO> request = HttpRequest.
                PUT("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateNullEmploymentPreferences() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPostWithNullPosition() {
        EmploymentDesiredAvailabilityCreateDTO requestBody = mkCreateEmploymentDesiredAvailabilityDTO();
        requestBody.setDesiredPosition(null);

        final HttpRequest<EmploymentDesiredAvailabilityCreateDTO> request = HttpRequest.POST("", requestBody)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void testPutValidationFailures() {

        final HttpRequest<EmploymentDesiredAvailabilityDTO> request = HttpRequest.PUT("", new EmploymentDesiredAvailabilityDTO())
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
        EmploymentDesiredAvailabilityDTO testEmploymentPreferenecs = mkUpdateEmploymentDesiredAvailabilityDTO();
        testEmploymentPreferenecs.setId(null);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testEmploymentPreferenecs)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
        LOG.info(thrown.getResponse().getBody(JsonNode.class).toString());
        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        assertEquals("employmentDesiredAvailability.id: must not be null", error.asText());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testDeleteEmploymentPreferences() {

        EmploymentDesiredAvailability employmentPreferences = createADefaultEmploymentPreferences();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(employmentPreferences.getId().toString())
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is Deleted and not Terminated
        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", employmentPreferences.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No new employee profile for id " + employmentPreferences.getId(), error);
    }

}
