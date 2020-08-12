package com.objectcomputing.checkins.services.role;


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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class RoleControllerTest {

    @Inject
    @Client("/services/role")
    HttpClient client;
    @Inject
    private RoleServices roleServices;

    @MockBean(RoleServices.class)
    public RoleServices roleServices() {
        return mock(RoleServices.class);
    }

    @Test
    void testCreateARole() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        Role r = new Role(roleCreateDTO.getRole(), roleCreateDTO.getMemberid());

        when(roleServices.save(eq(r))).thenReturn(r);

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(r, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), r.getId()), response.getHeaders().get("location"));

        verify(roleServices, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateForbidden() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        Role r = new Role(roleCreateDTO.getRole(), roleCreateDTO.getMemberid());

        when(roleServices.save(eq(r))).thenReturn(r);

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(roleServices, never()).read(any(UUID.class));
    }


    @Test
    void testCreateAnInvalidRole() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();

        Role r = new Role(RoleType.MEMBER, UUID.randomUUID());
        when(roleServices.save(any(Role.class))).thenReturn(r);

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("role.memberid: must not be null", errorList.get(0));
        assertEquals("role.role: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(roleServices, never()).save(any(Role.class));
    }

    @Test
    void testCreateANullRole() {
        Role r = new Role(RoleType.MEMBER, UUID.randomUUID());
        when(roleServices.save(any(Role.class))).thenReturn(r);

        final HttpRequest<String> request = HttpRequest.POST("", "")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [role] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(roleServices, never()).save(any(Role.class));
    }

    @Test
    void testLoadRoles() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        RoleCreateDTO roleCreateDTO2 = new RoleCreateDTO();
        roleCreateDTO2.setRole(RoleType.MEMBER);
        roleCreateDTO2.setMemberid(UUID.randomUUID());

        List<RoleCreateDTO> dtoList = List.of(roleCreateDTO, roleCreateDTO2);

        Role r = new Role(roleCreateDTO.getRole(), roleCreateDTO.getMemberid());
        Role r2 = new Role(roleCreateDTO2.getRole(), roleCreateDTO2.getMemberid());

        List<Role> roleList = List.of(r, r2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            Role thisG = roleList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(roleServices).save(any(Role.class));

        final MutableHttpRequest<List<RoleCreateDTO>> request = HttpRequest.POST("roles", dtoList)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<List<Role>> response = client.toBlocking().exchange(request, Argument.listOf(Role.class));

        assertEquals(roleList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(roleServices, times(2)).save(any(Role.class));
    }

    @Test
    void testLoadForbidden() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        List<Role> roles = List.of(r);

        final HttpRequest<List<Role>> request = HttpRequest.POST("roles", roles)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(roleServices, never()).save(any(Role.class));
    }

    @Test
    void testLoadRolesInvalidRole() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        RoleCreateDTO roleCreateDTO2 = new RoleCreateDTO();

        List<RoleCreateDTO> dtoList = List.of(roleCreateDTO, roleCreateDTO2);

        final MutableHttpRequest<List<RoleCreateDTO>> request = HttpRequest.POST("roles", dtoList)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("roles.memberid: must not be null", errorList.get(0));
        assertEquals("roles.role: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(roleServices, never()).save(any(Role.class));
    }

    @Test
    void testLoadRolesThrowException() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        RoleCreateDTO roleCreateDTO2 = new RoleCreateDTO();
        roleCreateDTO2.setRole(RoleType.MEMBER);
        roleCreateDTO2.setMemberid(UUID.randomUUID());

        List<RoleCreateDTO> dtoList = List.of(roleCreateDTO, roleCreateDTO2);

        Role r = new Role(roleCreateDTO.getRole(), roleCreateDTO.getMemberid());
        Role r2 = new Role(roleCreateDTO2.getRole(), roleCreateDTO2.getMemberid());

        final String errorMessage = "error message!";
        when(roleServices.save(eq(r))).thenReturn(r);

        when(roleServices.save(eq(r2))).thenAnswer(a -> {
            throw new RoleBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<RoleCreateDTO>> request = HttpRequest.POST("roles", dtoList)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s was not given role %s because: %s\"]",
                r2.getMemberid(), r2.getRole(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(roleServices, times(2)).save(any(Role.class));
    }

    @Test
    void testReadRole() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(roleServices.read(eq(r.getId()))).thenReturn(r);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", r.getId().toString())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(r, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(roleServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadForbidden() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());
        when(roleServices.read(eq(r.getId()))).thenReturn(r);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", r.getId().toString()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(roleServices, never()).read(any(UUID.class));
    }

    @Test
    void testReadRoleNotFound() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(roleServices.read(eq(r.getId()))).thenReturn(null);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", r.getId().toString()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Role.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(roleServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindRoles() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());
        Set<Role> roles = Collections.singleton(r);

        when(roleServices.findByFields(eq(r.getRole()), eq(r.getMemberid()))).thenReturn(roles);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", r.getRole(),
                r.getMemberid())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));

        assertEquals(roles, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(roleServices, times(1)).findByFields(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testFindRolesForbidden() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());
        Set<Role> roles = Collections.singleton(r);

        when(roleServices.findByFields(eq(r.getRole()), eq(r.getMemberid()))).thenReturn(roles);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", r.getRole(),
                r.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(roleServices, never()).findByFields(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testFindRolesAllParams() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());
        Set<Role> roles = Collections.singleton(r);

        when(roleServices.findByFields(eq(r.getRole()), eq(r.getMemberid()))).thenReturn(roles);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", r.getRole(),
                r.getMemberid())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));

        assertEquals(roles, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(roleServices, times(1)).findByFields(any(RoleType.class), any(UUID.class));
    }


    @Test
    void testFindRolesNull() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(roleServices.findByFields(eq(r.getRole()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s", r.getRole()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(Role.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(roleServices, times(1)).findByFields(any(RoleType.class), eq(null));
    }


    @Test
    void testUpdateRole() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(roleServices.update(eq(r))).thenReturn(r);

        final HttpRequest<Role> request = HttpRequest.PUT("", r)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(r, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), r.getId()), response.getHeaders().get("location"));

        verify(roleServices, times(1)).update(any(Role.class));
    }

    @Test
    void testUpdateForbidden() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(roleServices.update(eq(r))).thenReturn(r);

        final HttpRequest<Role> request = HttpRequest.PUT("", r)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());

        verify(roleServices, never()).update(any(Role.class));
    }

    @Test
    void testUpdateAnInvalidRole() {
        Role r = new Role(UUID.randomUUID(), null, null);

        when(roleServices.update(any(Role.class))).thenReturn(r);

        final HttpRequest<Role> request = HttpRequest.PUT("", r)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("role.memberid: must not be null", errorList.get(0));
        assertEquals("role.role: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(roleServices, never()).update(any(Role.class));
    }

    @Test
    void testUpdateANullRole() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());
        when(roleServices.update(any(Role.class))).thenReturn(r);

        final HttpRequest<String> request = HttpRequest.PUT("", "")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [role] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(roleServices, never()).update(any(Role.class));
    }


    @Test
    void testUpdateRoleThrowException() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        final String errorMessage = "error message!";

        when(roleServices.update(any(Role.class))).thenAnswer(a -> {
            throw new RoleBadArgException(errorMessage);
        });

        final MutableHttpRequest<Role> request = HttpRequest.PUT("", r)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(roleServices, times(1)).update(any(Role.class));
    }

    @Test
    void deleteRole() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(roleServices).delete(any(UUID.class));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid.toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        verify(roleServices, times(1)).delete(any(UUID.class));
    }

    @Test
    void deleteRoleUnauthorized() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(roleServices).delete(any(UUID.class));

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
        verify(roleServices, never()).delete(any(UUID.class));
    }
}
