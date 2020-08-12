package com.objectcomputing.checkins.services.role;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestcontainerSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

class RoleControllerTest extends TestcontainerSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/role")
    HttpClient client;

    @Test
    void testCreateARole() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(memberProfile.getUuid());

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        Role role = response.body();
        assertNotNull(role);
        assertEquals(roleCreateDTO.getMemberid(), role.getMemberid());
        assertEquals(roleCreateDTO.getRole(), role.getRole());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), role.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateForbidden() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateAnInvalidRole() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();

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
    }

    @Test
    void testCreateANullRole() {
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
    }

    @Test
    void testLoadRoles() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER);
        roleCreateDTO.setMemberid(memberProfile.getUuid());

        RoleCreateDTO roleCreateDTO2 = new RoleCreateDTO();
        roleCreateDTO2.setRole(RoleType.ADMIN);
        roleCreateDTO2.setMemberid(memberProfile.getUuid());

        List<RoleCreateDTO> dtoList = List.of(roleCreateDTO, roleCreateDTO2);

        final MutableHttpRequest<List<RoleCreateDTO>> request = HttpRequest.POST("roles", dtoList)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<List<Role>> response = client.toBlocking().exchange(request, Argument.listOf(Role.class));

        List<Role> roles = response.body();
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals(roleCreateDTO.getMemberid(), roles.get(0).getMemberid());
        assertEquals(roleCreateDTO.getRole(), roles.get(0).getRole());
        assertEquals(roleCreateDTO2.getMemberid(), roles.get(1).getMemberid());
        assertEquals(roleCreateDTO2.getRole(), roles.get(1).getRole());

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));
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
    }

    @Test
    void testReadRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultRoleRepository(memberProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", role.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(role, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadForbidden() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultRoleRepository(memberProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", role.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testReadRoleNotFound() {
        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Role.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindRoles() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Set<Role> roles = Set.of(createDefaultRoleRepository(RoleType.ADMIN, memberProfile),
                createDefaultRoleRepository(RoleType.PDL, memberProfile));

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", memberProfile.getUuid()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));

        assertEquals(roles, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRolesForbidden() {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", RoleType.ADMIN,
                UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testFindRolesAllParams() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultRoleRepository(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", role.getRole(),
                role.getMemberid())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));

        assertEquals(Set.of(role), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRolesNull() {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s", RoleType.ADMIN))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(), response.body());
    }

    @Test
    void testUpdateRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultRoleRepository(memberProfile);

        final HttpRequest<Role> request = HttpRequest.PUT("", role)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(role, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), role.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateForbidden() {
        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        final HttpRequest<Role> request = HttpRequest.PUT("", r)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateAnInvalidRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultRoleRepository(memberProfile);
        role.setMemberid(null);
        role.setRole(null);

        final HttpRequest<Role> request = HttpRequest.PUT("", role)
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
    }

    @Test
    void testUpdateANullRole() {
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
    }

    @Test
    void deleteRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultRoleRepository(memberProfile);

        assertNotNull(findRole(role));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(role.getId().toString())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRole(role));
    }

    @Test
    void deleteRoleUnauthorized() {
        UUID uuid = UUID.randomUUID();

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }
}
