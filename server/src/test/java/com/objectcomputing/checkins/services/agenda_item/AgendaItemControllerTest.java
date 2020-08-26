package com.objectcomputing.checkins.services.agenda_item;


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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class AgendaItemControllerTest {

    @Inject
    @Client("/services/agenda-item")
    HttpClient client;
    @Inject
    private AgendaItemServices agendaItemServices;

    @MockBean(AgendaItemServices.class)
    public AgendaItemServices agendaItemServices() {
        return mock(AgendaItemServices.class);
    }

    @Test
    void testCreateAnAgendaItem() {
        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO.setDescription("dnc");

        AgendaItem a = new AgendaItem(agendaItemCreateDTO.getCheckinid(), agendaItemCreateDTO.getCreatedbyid(), agendaItemCreateDTO.getDescription());

        when(agendaItemServices.save(eq(a))).thenReturn(a);

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(a, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), a.getId()), response.getHeaders().get("location"));

        verify(agendaItemServices, times(1)).save(any(AgendaItem.class));
    }

    @Test
    void testCreateAnInvalidAgendaItem() {
        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();

        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(agendaItemServices.save(any(AgendaItem.class))).thenReturn(a);

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("agendaItem.checkinid: must not be null", errorList.get(0));
        assertEquals("agendaItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(agendaItemServices, never()).save(any(AgendaItem.class));
    }

    @Test
    void testCreateANullAgendaItem() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(agendaItemServices.save(any(AgendaItem.class))).thenReturn(a);

        final HttpRequest<String> request = HttpRequest.POST("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [agendaItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(agendaItemServices, never()).save(any(AgendaItem.class));
    }

    @Test
    void testLoadAgendaItems() {
        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO.setDescription("dnc");

        AgendaItemCreateDTO agendaItemCreateDTO2 = new AgendaItemCreateDTO();
        agendaItemCreateDTO2.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO2.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO2.setDescription("dnc");

        List<AgendaItemCreateDTO> dtoList = List.of(agendaItemCreateDTO, agendaItemCreateDTO2);

        AgendaItem a = new AgendaItem(agendaItemCreateDTO.getCheckinid(), agendaItemCreateDTO.getCreatedbyid(), agendaItemCreateDTO.getDescription());
        AgendaItem a2 = new AgendaItem(agendaItemCreateDTO2.getCheckinid(), agendaItemCreateDTO2.getCreatedbyid(), agendaItemCreateDTO2.getDescription());

        List<AgendaItem> checkinList = List.of(a, a2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(ans -> {
            AgendaItem thisC = checkinList.get(i.getAndAdd(1));
            assertEquals(thisC, ans.getArgument(0));
            return thisC;
        }).when(agendaItemServices).save(any(AgendaItem.class));

        final MutableHttpRequest<List<AgendaItemCreateDTO>> request = HttpRequest.POST("items", dtoList);
        final HttpResponse<List<AgendaItem>> response = client.toBlocking().exchange(request, Argument.listOf(AgendaItem.class));

        assertEquals(checkinList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(agendaItemServices, times(2)).save(any(AgendaItem.class));
    }

    @Test
    void testLoadAgendaItemsInvalidAgendaItem() {
        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO.setDescription("dnc");

        AgendaItemCreateDTO agendaItemCreateDTO2 = new AgendaItemCreateDTO();

        List<AgendaItemCreateDTO> dtoList = List.of(agendaItemCreateDTO, agendaItemCreateDTO2);

        final MutableHttpRequest<List<AgendaItemCreateDTO>> request = HttpRequest.POST("items", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("agendaItems.checkinid: must not be null", errorList.get(0));
        assertEquals("agendaItems.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(agendaItemServices, never()).save(any(AgendaItem.class));
    }

    @Test
    void testLoadAgendaItemsThrowException() {
        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO.setDescription("dnc");

        AgendaItemCreateDTO agendaItemCreateDTO2 = new AgendaItemCreateDTO();
        agendaItemCreateDTO2.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO2.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO2.setDescription("dnc");

        List<AgendaItemCreateDTO> dtoList = List.of(agendaItemCreateDTO, agendaItemCreateDTO2);

        AgendaItem a = new AgendaItem(agendaItemCreateDTO.getCheckinid(), agendaItemCreateDTO.getCreatedbyid(), agendaItemCreateDTO.getDescription());
        AgendaItem a2 = new AgendaItem(agendaItemCreateDTO2.getCheckinid(), agendaItemCreateDTO2.getCreatedbyid(), agendaItemCreateDTO2.getDescription());

        final String errorMessage = "error message!";
        when(agendaItemServices.save(eq(a))).thenReturn(a);

        when(agendaItemServices.save(eq(a2))).thenAnswer(ans -> {
            throw new AgendaItemBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<AgendaItemCreateDTO>> request = HttpRequest.POST("items", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s's agenda item was not added to CheckIn %s because: %s\"]",
                a2.getCreatedbyid(), a2.getCheckinid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(agendaItemServices, times(2)).save(any(AgendaItem.class));
    }

    @Test
    void deleteAgendaItem() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(agendaItemServices).delete(any(UUID.class));

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        verify(agendaItemServices, times(1)).delete(any(UUID.class));
    }

    @Test
    void testReadAgendaItem() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(agendaItemServices.read(eq(a.getId()))).thenReturn(a);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", a.getId().toString()));
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(a, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(agendaItemServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadAgendaItemNotFound() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(agendaItemServices.read(eq(a.getCheckinid()))).thenReturn(a);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", a.getId().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, AgendaItem.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(agendaItemServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindAgendaItems() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        Set<AgendaItem> checkins = Collections.singleton(a);

        when(agendaItemServices.findByFields(eq(a.getCheckinid()), eq(a.getCreatedbyid()))).thenReturn(checkins);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", a.getCheckinid(),
                a.getCreatedbyid()));
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(checkins, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(agendaItemServices, times(1)).findByFields(any(UUID.class), any(UUID.class));
    }

    @Test
    void testFindAgendaItemsNull() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(agendaItemServices.findByFields(eq(a.getCheckinid()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", a.getCheckinid()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(agendaItemServices, times(1)).findByFields(any(UUID.class), eq(null));
    }


    @Test
    void testUpdateAgendaItem() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(agendaItemServices.update(eq(a))).thenReturn(a);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", a);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(a, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), a.getId()), response.getHeaders().get("location"));

        verify(agendaItemServices, times(1)).update(any(AgendaItem.class));
    }

    @Test
    void testUpdateAnInvalidAgendaItem() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), null, null, "dnc");

        when(agendaItemServices.update(any(AgendaItem.class))).thenReturn(a);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", a);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("agendaItem.checkinid: must not be null", errorList.get(0));
        assertEquals("agendaItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(agendaItemServices, never()).update(any(AgendaItem.class));
    }

    @Test
    void testUpdateANullAgendaItem() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(agendaItemServices.update(any(AgendaItem.class))).thenReturn(a);

        final HttpRequest<String> request = HttpRequest.PUT("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [agendaItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(agendaItemServices, never()).update(any(AgendaItem.class));
    }


    @Test
    void testUpdateAgendaItemThrowException() {
        AgendaItem a = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        final String errorMessage = "error message!";

        when(agendaItemServices.update(any(AgendaItem.class))).thenAnswer(ans -> {
            throw new AgendaItemBadArgException(errorMessage);
        });

        final MutableHttpRequest<AgendaItem> request = HttpRequest.PUT("", a);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(agendaItemServices, times(1)).update(any(AgendaItem.class));
    }

}
