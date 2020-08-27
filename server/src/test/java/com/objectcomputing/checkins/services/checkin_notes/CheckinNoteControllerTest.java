package com.objectcomputing.checkins.services.checkin_notes;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
public class CheckinNoteControllerTest {

    @Inject
    @Client("/services/checkin-note")
    HttpClient client;

    @Inject
    private CheckinNoteServices checkinNoteServices;

    @MockBean(CheckinNoteServices.class)
    public CheckinNoteServices checkinNoteServices() {
        return mock(CheckinNoteServices.class);
    }

    @Test
    void testCreateCheckinNote() {
        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(UUID.randomUUID());
        checkinNoteCreateDTO.setCreatedbyid(UUID.randomUUID());
        checkinNoteCreateDTO.setDescription("test");

        CheckinNote cNote = new CheckinNote(checkinNoteCreateDTO.getCheckinid(), checkinNoteCreateDTO.getCreatedbyid(), checkinNoteCreateDTO.getDescription());

        when(checkinNoteServices.save(eq(cNote))).thenReturn(cNote);

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(cNote, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), cNote.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateInvalidCheckinNote() {
        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();

        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinNoteServices.save(eq(cNote))).thenReturn(cNote);

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinNote.checkinid: must not be null", errorList.get(0));
        assertEquals("checkinNote.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateNullCheckinNote() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinNoteServices.save(eq(cNote))).thenReturn(cNote);

        final HttpRequest<String> request = HttpRequest.POST("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkinNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testReadCheckinNote() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinNoteServices.read(cNote.getId())).thenReturn(cNote);
        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", cNote.getId().toString()));
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(cNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadCheckinNoteNotFound() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinNoteServices.read(cNote.getId())).thenReturn(null);
        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", cNote.getId().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, CheckinNote.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

    }

    @Test
    void testFindCheckinNote() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        Set<CheckinNote> checkinNote = Collections.singleton(cNote);

        when(checkinNoteServices.findByFields(cNote.getCheckinid(), cNote.getCreatedbyid())).thenReturn(checkinNote);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", cNote.getCheckinid(), cNote.getCreatedbyid()));
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteNull() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        when(checkinNoteServices.findByFields(eq(cNote.getCheckinid()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", cNote.getCheckinid()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

    }

    @Test
    void testUpdateCheckinNote() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinNoteServices.update(eq(cNote))).thenReturn(cNote);

        final HttpRequest<?> request = HttpRequest.PUT("", cNote);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(cNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateInvalidCheckinNote() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), null, null, "test");
        when(checkinNoteServices.update(any(CheckinNote.class))).thenReturn(cNote);

        final HttpRequest<?> request = HttpRequest.PUT("", cNote);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));


        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinNote.checkinid: must not be null", errorList.get(0));
        assertEquals("checkinNote.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateNullCheckinNote() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        when(checkinNoteServices.update(any(CheckinNote.class))).thenReturn(cNote);

        final HttpRequest<?> request = HttpRequest.PUT("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateCheckinNoteThrowException() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        final String errorMessage = "error message";
        when(checkinNoteServices.update(any(CheckinNote.class))).thenAnswer(ans -> {
            throw new CheckinNotesBadArgException(errorMessage);
        });

        final MutableHttpRequest<CheckinNote> request = HttpRequest.PUT("", cNote);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

}