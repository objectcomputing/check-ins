package com.objectcomputing.checkins.services.pulseresponse;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

import java.time.LocalDate;

@MicronautTest
public class PulseResponseControllerTest {

    @Inject
    @Client("/services/pulse-response")
    HttpClient client;

    @Inject
    private PulseResponseService pulseResponseService;

    @MockBean(PulseResponseService.class)
    public PulseResponseService pulseResponseService() {
        return mock(PulseResponseService.class);
    }

    @Test
    void testCreateAPulseResponse() {
        PulseResponseCreateDTO pulseResponseCreateDTO = new PulseResponseCreateDTO();
        pulseResponseCreateDTO.setSubmissionDate(LocalDate.of(2019, 1, 01));
        pulseResponseCreateDTO.setUpdatedDate(LocalDate.of(2019, 1, 01));
        pulseResponseCreateDTO.setTeamMemberId(UUID.randomUUID());
        pulseResponseCreateDTO.setInternalFeelings("doc1");
        pulseResponseCreateDTO.setExternalFeelings("doc2");

        PulseResponse cd = new PulseResponse(pulseResponseCreateDTO.getSubmissionDate(),pulseResponseCreateDTO.getUpdatedDate(), pulseResponseCreateDTO.getTeamMemberId(), pulseResponseCreateDTO.getInternalFeelings(), pulseResponseCreateDTO.getExternalFeelings());

        when(pulseResponseService.save(eq(cd))).thenReturn(cd);

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(cd, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), cd.getId()), response.getHeaders().get("location"));

        verify(pulseResponseService, times(1)).save(any(PulseResponse.class));
    }

    @Test
    void testCreateAnInvalidPulseResponse() {
        PulseResponseCreateDTO pulseResponseCreateDTO = new PulseResponseCreateDTO();

        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "exampleId", "exampleId");
        when(pulseResponseService.save(any(PulseResponse.class))).thenReturn(cd);

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("pulseResponse.internalFeelings: must not be null", errorList.get(0));
        assertEquals("pulseResponse.teamMemberId: must not be null", errorList.get(1));
        assertEquals("pulseResponse.externalFeelings: must not be null", errorList.get(2));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(pulseResponseService, never()).save(any(PulseResponse.class));
    }

    @Test
    void testCreateANullPulseResponse() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(),"exampleId","exampleId");
        when(pulseResponseService.save(any(PulseResponse.class))).thenReturn(cd);

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [pulseResponse] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(pulseResponseService, never()).save(any(PulseResponse.class));
    }

    @Test
    void testCreatePulseResponseThrowsExceptionForMemberRole() {

        PulseResponseCreateDTO pulseResponseCreateDTO = new PulseResponseCreateDTO();
        pulseResponseCreateDTO.setTeamMemberId(UUID.randomUUID());
        pulseResponseCreateDTO.setInternalFeelings("doc1");

        final HttpRequest<?> request = HttpRequest.POST("", pulseResponseCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(pulseResponseService, times(0)).save(any(PulseResponse.class));
    }

    @Test
    void testFindPulseResponse() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "ExampleDocId", "ExampleDocId");
        Set<PulseResponse> pulseResponses = Collections.singleton(cd);

        when(pulseResponseService.read(eq(cd.getTeamMemberId()))).thenReturn(pulseResponses);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", cd.getTeamMemberId())).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(pulseResponses, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(pulseResponseService, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindPulseResponseNull() {

        when(pulseResponseService.read(eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=" + null)).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testFindPulseResponseThrowsExceptionForMemberRole() {

        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "ExampleDocId", "ExampleDocId");

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=" + cd.getTeamMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(pulseResponseService, times(0)).read(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponse() {

        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "exampleId", "exampleId");

        when(pulseResponseService.update(eq(cd))).thenReturn(cd);

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", cd).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(cd, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), cd.getId()), response.getHeaders().get("location"));

        verify(pulseResponseService, times(1)).update(any(PulseResponse.class));
    }

    @Test
    void testUpdateAnInvalidPulseResponse() {
        PulseResponse pulseResponse = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), null, null);

        when(pulseResponseService.update(any(PulseResponse.class))).thenReturn(pulseResponse);

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseResponse).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(pulseResponseService, never()).update(any(PulseResponse.class));
    }

    @Test
    void testUpdateANullPulseResponse() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "exampleId", "exampleId");
        when(pulseResponseService.update(any(PulseResponse.class))).thenReturn(cd);

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [pulseResponse] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(pulseResponseService, never()).update(any(PulseResponse.class));
    }

    @Test
    void testUpdatePulseResponseThrowException() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "exampleId", "exampleId");

        final String errorMessage = "error message!";

        when(pulseResponseService.update(any(PulseResponse.class))).thenAnswer(ans -> {
            throw new PulseResponseBadArgException(errorMessage);
        });

        final MutableHttpRequest<PulseResponse> request = HttpRequest.PUT("", cd).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(pulseResponseService, times(1)).update(any(PulseResponse.class));
    }

    @Test
    void testUpdatePulseResponseThrowsExceptionForMemberRole() {

        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "ExampleDocId", "ExampleDocId");

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", cd).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(pulseResponseService, times(0)).update(any(PulseResponse.class));
    }

    @Test
    void deletePulseResponseThrowsException() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<PulseResponse> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());

        verify(pulseResponseService, times(0)).delete(any(UUID.class));
    }

    @Test
    void deletePulseResponseThrowsExceptionForPdlRole() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(pulseResponseService, times(0)).delete(any(UUID.class));
    }

    @Test
    void deletePulseResponseThrowsExceptionForMemberRole() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(pulseResponseService, times(0)).delete(any(UUID.class));
    }

    @Test
    void deletePulseResponseIfAdmin() {

        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(pulseResponseService).delete(any(UUID.class));

        final HttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        verify(pulseResponseService, times(1)).delete(any(UUID.class));
    }
}
