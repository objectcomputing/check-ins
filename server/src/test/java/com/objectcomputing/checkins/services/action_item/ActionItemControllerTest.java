package com.objectcomputing.checkins.services.action_item;


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
class ActionItemControllerTest {

    @Inject
    @Client("/services/action-item")
    HttpClient client;
    @Inject
    private ActionItemServices actionItemServices;

    @MockBean(ActionItemServices.class)
    public ActionItemServices actionItemServices() {
        return mock(ActionItemServices.class);
    }

    @Test
    void testCreateAnActionItem() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO.setDescription("dnc");

        ActionItem a = new ActionItem(actionItemCreateDTO.getCheckinid(), actionItemCreateDTO.getCreatedbyid(), actionItemCreateDTO.getDescription());

        when(actionItemServices.save(eq(a))).thenReturn(a);

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(a, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), a.getId()), response.getHeaders().get("location"));

        verify(actionItemServices, times(1)).save(any(ActionItem.class));
    }

    @Test
    void testCreateAnInvalidActionItem() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();

        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(actionItemServices.save(any(ActionItem.class))).thenReturn(a);

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("actionItem.checkinid: must not be null", errorList.get(0));
        assertEquals("actionItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(actionItemServices, never()).save(any(ActionItem.class));
    }

    @Test
    void testCreateANullActionItem() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(actionItemServices.save(any(ActionItem.class))).thenReturn(a);

        final HttpRequest<String> request = HttpRequest.POST("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [actionItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(actionItemServices, never()).save(any(ActionItem.class));
    }

    @Test
    void testLoadActionItems() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO.setDescription("dnc");

        ActionItemCreateDTO actionItemCreateDTO2 = new ActionItemCreateDTO();
        actionItemCreateDTO2.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO2.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO2.setDescription("dnc");

        List<ActionItemCreateDTO> dtoList = List.of(actionItemCreateDTO, actionItemCreateDTO2);

        ActionItem a = new ActionItem(actionItemCreateDTO.getCheckinid(), actionItemCreateDTO.getCreatedbyid(), actionItemCreateDTO.getDescription());
        ActionItem a2 = new ActionItem(actionItemCreateDTO2.getCheckinid(), actionItemCreateDTO2.getCreatedbyid(), actionItemCreateDTO2.getDescription());

        List<ActionItem> checkinList = List.of(a, a2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(ans -> {
            ActionItem thisG = checkinList.get(i.getAndAdd(1));
            assertEquals(thisG, ans.getArgument(0));
            return thisG;
        }).when(actionItemServices).save(any(ActionItem.class));

        final MutableHttpRequest<List<ActionItemCreateDTO>> request = HttpRequest.POST("items", dtoList);
        final HttpResponse<List<ActionItem>> response = client.toBlocking().exchange(request, Argument.listOf(ActionItem.class));

        assertEquals(checkinList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(actionItemServices, times(2)).save(any(ActionItem.class));
    }

    @Test
    void testLoadActionItemsInvalidActionItem() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO.setDescription("dnc");

        ActionItemCreateDTO actionItemCreateDTO2 = new ActionItemCreateDTO();

        List<ActionItemCreateDTO> dtoList = List.of(actionItemCreateDTO, actionItemCreateDTO2);

        final MutableHttpRequest<List<ActionItemCreateDTO>> request = HttpRequest.POST("items", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("actionItems.checkinid: must not be null", errorList.get(0));
        assertEquals("actionItems.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(actionItemServices, never()).save(any(ActionItem.class));
    }

    @Test
    void testLoadActionItemsThrowException() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO.setDescription("dnc");

        ActionItemCreateDTO actionItemCreateDTO2 = new ActionItemCreateDTO();
        actionItemCreateDTO2.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO2.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO2.setDescription("dnc");

        List<ActionItemCreateDTO> dtoList = List.of(actionItemCreateDTO, actionItemCreateDTO2);

        ActionItem a = new ActionItem(actionItemCreateDTO.getCheckinid(), actionItemCreateDTO.getCreatedbyid(), actionItemCreateDTO.getDescription());
        ActionItem a2 = new ActionItem(actionItemCreateDTO2.getCheckinid(), actionItemCreateDTO2.getCreatedbyid(), actionItemCreateDTO2.getDescription());

        final String errorMessage = "error message!";
        when(actionItemServices.save(eq(a))).thenReturn(a);

        when(actionItemServices.save(eq(a2))).thenAnswer(ans -> {
            throw new ActionItemBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<ActionItemCreateDTO>> request = HttpRequest.POST("items", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s's action item was not added to CheckIn %s because: %s\"]",
                a2.getCreatedbyid(), a2.getCheckinid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(actionItemServices, times(2)).save(any(ActionItem.class));
    }

    @Test
    void deleteActionItem() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(actionItemServices).delete(any(UUID.class));

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        verify(actionItemServices, times(1)).delete(any(UUID.class));
    }

    @Test
    void testReadAllActionItem() {
        Set<ActionItem> actionItems = Set.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(actionItemServices.readAll()).thenReturn(actionItems);

        final HttpRequest<UUID> request = HttpRequest.GET("all");
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(actionItems, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(actionItemServices, times(1)).readAll();
    }

    @Test
    void testReadActionItem() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(actionItemServices.read(eq(a.getId()))).thenReturn(a);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", a.getId().toString()));
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(a, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(actionItemServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadActionItemNotFound() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(actionItemServices.read(eq(a.getCheckinid()))).thenReturn(a);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", a.getId().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, ActionItem.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(actionItemServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindActionItems() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        Set<ActionItem> checkins = Collections.singleton(a);

        when(actionItemServices.findByFields(eq(a.getCheckinid()), eq(a.getCreatedbyid()))).thenReturn(checkins);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", a.getCheckinid(),
                a.getCreatedbyid()));
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(checkins, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(actionItemServices, times(1)).findByFields(any(UUID.class), any(UUID.class));
    }

    @Test
    void testFindActionItemsNull() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(actionItemServices.findByFields(eq(a.getCheckinid()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", a.getCheckinid()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(ActionItem.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(actionItemServices, times(1)).findByFields(any(UUID.class), eq(null));
    }


    @Test
    void testUpdateActionItem() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(actionItemServices.update(eq(a))).thenReturn(a);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", a);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(a, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), a.getId()), response.getHeaders().get("location"));

        verify(actionItemServices, times(1)).update(any(ActionItem.class));
    }

    @Test
    void testUpdateAnInvalidActionItem() {
        ActionItem a = new ActionItem(UUID.randomUUID(), null, null, "dnc");

        when(actionItemServices.update(any(ActionItem.class))).thenReturn(a);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", a);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("actionItem.checkinid: must not be null", errorList.get(0));
        assertEquals("actionItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(actionItemServices, never()).update(any(ActionItem.class));
    }

    @Test
    void testUpdateANullActionItem() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(actionItemServices.update(any(ActionItem.class))).thenReturn(a);

        final HttpRequest<String> request = HttpRequest.PUT("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [actionItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(actionItemServices, never()).update(any(ActionItem.class));
    }


    @Test
    void testUpdateActionItemThrowException() {
        ActionItem a = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        final String errorMessage = "error message!";

        when(actionItemServices.update(any(ActionItem.class))).thenAnswer(ans -> {
            throw new ActionItemBadArgException(errorMessage);
        });

        final MutableHttpRequest<ActionItem> request = HttpRequest.PUT("", a);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(actionItemServices, times(1)).update(any(ActionItem.class));
    }

}
