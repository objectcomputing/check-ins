package com.objectcomputing.checkins.services.memberskills;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.memberSkill.MemberSkill;
import com.objectcomputing.checkins.services.memberSkill.MemberSkillCreateDTO;
import com.objectcomputing.checkins.services.memberSkill.MemberSkillServices;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MicronautTest
public class MemberSkillControllerTest {


    @Inject
    @Client("/services/member-skill")
    HttpClient client;
    @Inject
    private MemberSkillServices memberSkillServices;

    @MockBean(MemberSkillServices.class)
    public MemberSkillServices memberSkillsServices() {
        return mock(MemberSkillServices.class);
    }


    @Test
    void testCreateAMemberSkill() {
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(UUID.randomUUID());
        memberSkillCreateDTO.setSkillid(UUID.randomUUID());

        MemberSkill m = new MemberSkill(memberSkillCreateDTO.getMemberid(),
                memberSkillCreateDTO.getSkillid());

        when(memberSkillServices.save(eq(m))).thenReturn(m);

        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO);
        final HttpResponse<MemberSkill> response = client.toBlocking().exchange(request, MemberSkill.class);

        assertEquals(m, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), m.getId()), response.getHeaders().get("location"));

        verify(memberSkillServices, times(1)).save(any(MemberSkill.class));
    }

    @Test
    void testCreateAnInvalidMemberSkill() {
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();

        MemberSkill m = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());
        when(memberSkillServices.save(any(MemberSkill.class))).thenReturn(m);

        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("memberSkill.memberid: must not be null", errorList.get(0));
        assertEquals("memberSkill.skillid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(memberSkillServices, never()).save(any(MemberSkill.class));
    }

    @Test
    void testCreateANullMemberSkill() {
        MemberSkill m = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());
        when(memberSkillServices.save(any(MemberSkill.class))).thenReturn(m);

        final HttpRequest<String> request = HttpRequest.POST("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [memberSkill] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(memberSkillServices, never()).save(any(MemberSkill.class));
    }

    @Test
    void deleteMemberSkill() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(memberSkillServices).delete(any(UUID.class));

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        verify(memberSkillServices, times(1)).delete(any(UUID.class));
    }

    @Test
    void testReadAllMemberSkills() {
        Set<MemberSkill> memberSkills = Set.of(
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        );

        when(memberSkillServices.readAll()).thenReturn(memberSkills);

        final HttpRequest<UUID> request = HttpRequest.GET("all");
        final HttpResponse<Set<MemberSkill>> response = client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class));

        assertEquals(memberSkills, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(memberSkillServices, times(1)).readAll();
    }

    @Test
    void testReadMemberSkill() {
        MemberSkill m = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(memberSkillServices.read(eq(m.getId()))).thenReturn(m);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", m.getId().toString()));
        final HttpResponse<MemberSkill> response = client.toBlocking().exchange(request, MemberSkill.class);

        assertEquals(m, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(memberSkillServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadMemberSkillNotFound() {
        MemberSkill m = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(memberSkillServices.read(eq(m.getId()))).thenReturn(null);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", m.getId().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, MemberSkill.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(memberSkillServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindMemberSkills() {
        MemberSkill m = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        Set<MemberSkill> memberSkillSet = Collections.singleton(m);

        when(memberSkillServices.findByFields(eq(m.getMemberid()), eq(m.getSkillid()))).thenReturn(memberSkillSet);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s&skillid=%s", m.getMemberid(),
                m.getSkillid()));
        final HttpResponse<Set<MemberSkill>> response = client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class));

        assertEquals(memberSkillSet, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(memberSkillServices, times(1)).findByFields(any(UUID.class), any(UUID.class));
    }

    @Test
    void testFindMemberSkillsNull() {
        MemberSkill m = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(memberSkillServices.findByFields(eq(m.getMemberid()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", m.getMemberid()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(memberSkillServices, times(1)).findByFields(any(UUID.class), eq(null));
    }


}
