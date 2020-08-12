package com.objectcomputing.checkins.services.checkindocument;

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

@MicronautTest
public class CheckinDocumentControllerTest {

    @Inject
    @Client("/services/checkin-document")
    HttpClient client;

    @Inject
    private CheckinDocumentServices checkinDocumentService;

    @MockBean(CheckinDocumentServices.class)
    public CheckinDocumentServices checkinDocumentService() {
        return mock(CheckinDocumentServices.class);
    }

    @Test
    void testCreateACheckinDocument() {
        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(UUID.randomUUID());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        CheckinDocument cd = new CheckinDocument(checkinDocumentCreateDTO.getCheckinsId(), checkinDocumentCreateDTO.getUploadDocId());

        when(checkinDocumentService.save(eq(cd))).thenReturn(cd);

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<CheckinDocument> response = client.toBlocking().exchange(request, CheckinDocument.class);

        assertEquals(cd, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), cd.getId()), response.getHeaders().get("location"));

        verify(checkinDocumentService, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testCreateAnInvalidCheckinDocument() {
        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();

        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "exampleId");
        when(checkinDocumentService.save(any(CheckinDocument.class))).thenReturn(cd);

        final HttpRequest<CheckinDocumentCreateDTO> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinDocument.checkinsId: must not be null", errorList.get(0));
        assertEquals("checkinDocument.uploadDocId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(checkinDocumentService, never()).save(any(CheckinDocument.class));
    }

    @Test
    void testCreateANullCheckinDocument() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(),"exampleId");
        when(checkinDocumentService.save(any(CheckinDocument.class))).thenReturn(cd);

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinDocument] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(checkinDocumentService, never()).save(any(CheckinDocument.class));
    }

    @Test
    void testCreateCheckinDocumentThrowsExceptionForMemberRole() {

        CheckinDocumentCreateDTO checkinDocumentCreateDTO = new CheckinDocumentCreateDTO();
        checkinDocumentCreateDTO.setCheckinsId(UUID.randomUUID());
        checkinDocumentCreateDTO.setUploadDocId("doc1");

        final HttpRequest<?> request = HttpRequest.POST("", checkinDocumentCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(checkinDocumentService, times(0)).save(any(CheckinDocument.class));
    }

    @Test
    void testFindCheckinDocument() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "ExampleDocId");
        Set<CheckinDocument> checkinDocuments = Collections.singleton(cd);

        when(checkinDocumentService.read(eq(cd.getCheckinsId()))).thenReturn(checkinDocuments);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=%s", cd.getCheckinsId())).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<Set<CheckinDocument>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class));

        assertEquals(checkinDocuments, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(checkinDocumentService, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindCheckinDocumentNull() {

        when(checkinDocumentService.read(eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=" + null)).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testFindCheckinDocumentThrowsExceptionForMemberRole() {

        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "ExampleDocId");

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/?checkinsId=" + cd.getCheckinsId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(checkinDocumentService, times(0)).read(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocument() {

        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "exampleId");

        when(checkinDocumentService.update(eq(cd))).thenReturn(cd);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", cd).basicAuth(PDL_ROLE, PDL_ROLE);
        final HttpResponse<CheckinDocument> response = client.toBlocking().exchange(request, CheckinDocument.class);

        assertEquals(cd, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), cd.getId()), response.getHeaders().get("location"));

        verify(checkinDocumentService, times(1)).update(any(CheckinDocument.class));
    }

    @Test
    void testUpdateAnInvalidCheckinDocument() {
        CheckinDocument checkinDocument = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), null);

        when(checkinDocumentService.update(any(CheckinDocument.class))).thenReturn(checkinDocument);

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", checkinDocument).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(checkinDocumentService, never()).update(any(CheckinDocument.class));
    }

    @Test
    void testUpdateANullCheckinDocument() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "exampleId");
        when(checkinDocumentService.update(any(CheckinDocument.class))).thenReturn(cd);

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinDocument] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(checkinDocumentService, never()).update(any(CheckinDocument.class));
    }

    @Test
    void testUpdateCheckinDocumentThrowException() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "exampleId");

        final String errorMessage = "error message!";

        when(checkinDocumentService.update(any(CheckinDocument.class))).thenAnswer(ans -> {
            throw new CheckinDocumentBadArgException(errorMessage);
        });

        final MutableHttpRequest<CheckinDocument> request = HttpRequest.PUT("", cd).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(checkinDocumentService, times(1)).update(any(CheckinDocument.class));
    }

    @Test
    void testUpdateCheckinDocumentThrowsExceptionForMemberRole() {

        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "ExampleDocId");

        final HttpRequest<CheckinDocument> request = HttpRequest.PUT("", cd).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(CheckinDocument.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(checkinDocumentService, times(0)).update(any(CheckinDocument.class));
    }

    @Test
    void deleteCheckinDocumentThrowsException() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<CheckinDocument> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());

        verify(checkinDocumentService, times(0)).delete(any(UUID.class));
    }

    @Test
    void deleteCheckinDocumentThrowsExceptionForPdlRole() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(checkinDocumentService, times(0)).delete(any(UUID.class));
    }

    @Test
    void deleteCheckinDocumentThrowsExceptionForMemberRole() {

        UUID uuid = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(checkinDocumentService, times(0)).delete(any(UUID.class));
    }

    @Test
    void deleteCheckinDocumentIfAdmin() {

        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(checkinDocumentService).delete(any(UUID.class));

        final HttpRequest<?> request = HttpRequest.DELETE(uuid.toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        verify(checkinDocumentService, times(1)).delete(any(UUID.class));
    }
}
