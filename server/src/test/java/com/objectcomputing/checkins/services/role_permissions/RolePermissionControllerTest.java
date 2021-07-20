package com.objectcomputing.checkins.services.role_permissions;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RolePermissionFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionCreateDTO;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;
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

import static org.junit.jupiter.api.Assertions.*;

class RolePermissionControllerTest extends TestContainersSuite implements MemberProfileFixture, RolePermissionFixture {

    @Inject
    @Client("/services/permissions")
    HttpClient client;

    @Test
    void testCreateARole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.MEMBER);
        roleCreateDTO.setMemberid(memberProfile.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        RolePermission role = response.body();
        assertNotNull(role);
        assertEquals(roleCreateDTO.getMemberid(), role.getMemberid());
        assertEquals(roleCreateDTO.getRole(), role.getPermission());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), role.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateARoleAlreadyExists() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultAdminRole(unrelatedProfile);

        RolePermission alreadyExistingRole = createDefaultRolePermission(RolePermissionType.MEMBER, memberProfile);
        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(alreadyExistingRole.getPermission());
        roleCreateDTO.setMemberid(alreadyExistingRole.getMemberid());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s already has role %s", roleCreateDTO.getMemberid(), roleCreateDTO.getRole()),
                error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateForbidden() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultRolePermission(RolePermissionType.MEMBER, unrelatedProfile);

        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.MEMBER);
        roleCreateDTO.setMemberid(UUID.randomUUID());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateAnInvalidRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultAdminRole(unrelatedProfile);

        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
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
    void testCreateNonExistingMember() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        RolePermissionCreateDTO permission = new RolePermissionCreateDTO();
        permission.setMemberid(UUID.randomUUID());
        permission.setRole(RolePermissionType.MEMBER);


        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", permission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", permission.getMemberid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateANullRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultAdminRole(unrelatedProfile);

        final HttpRequest<String> request = HttpRequest.POST("", "")
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
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
    void testReadRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultRolePermission(RolePermissionType.MEMBER, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultAdminRole(memberProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", role.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        assertEquals(role, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadRoleNotFound() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultAdminRole(unrelatedProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, RolePermission.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindRoles() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Set<RolePermission> roles = Set.of(createDefaultRolePermission(RolePermissionType.ADMIN, memberProfile),
                createDefaultRolePermission(RolePermissionType.PDL, memberProfile));

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", memberProfile.getId()))
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
        final HttpResponse<Set<RolePermission>> response = client.toBlocking().exchange(request, Argument.setOf(RolePermission.class));

        assertEquals(roles, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRolesAllParams() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultAdminRole(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?permission=%s&memberid=%s", role.getPermission(),
                role.getMemberid())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpResponse<Set<RolePermission>> response = client.toBlocking().exchange(request, Argument.setOf(RolePermission.class));

        assertEquals(Set.of(role), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRolesDoesNotExist() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission role = createDefaultAdminRole(unrelatedProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?permission=%s", RolePermissionType.PDL))
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getPermission().name());
        HttpResponse<Set<RolePermission>> response = client.toBlocking().exchange(request, Argument.setOf(RolePermission.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(), response.body());
    }

    @Test
    void testUpdateRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultAdminRole(memberProfile);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        assertEquals(role, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), role.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateNonExistingMember() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultAdminRole(memberProfile);

        role.setMemberid(UUID.randomUUID());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", role.getMemberid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingRoleType() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission roleToUpdate = createDefaultAdminRole(memberProfile);

        Map<String, String> role = new HashMap<>();
        role.put("id", roleToUpdate.getId().toString());
        role.put("role", "ROLE_DOES_NOT_EXIST");
        role.put("memberid", roleToUpdate.getMemberid().toString());

        final HttpRequest<Map<String, String>> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertTrue(error.contains("not one of the values accepted for Enum class"));
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = new RolePermission(UUID.randomUUID(), RolePermissionType.MEMBER, memberProfile.getId());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate role to update with id %s", role.getId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateWithoutId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultRolePermission(RolePermissionType.MEMBER, memberProfile);
        role.setId(null);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("Unable to locate role to update with id null", error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateForbidden() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultRolePermission(RolePermissionType.MEMBER, unrelatedProfile);

        RolePermission r = new RolePermission(UUID.randomUUID(), RolePermissionType.ADMIN, UUID.randomUUID());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", r)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateAnInvalidRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultAdminRole(memberProfile);
        role.setMemberid(null);
        role.setPermission(null);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
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
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        final HttpRequest<String> request = HttpRequest.PUT("", "")
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String errors = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertEquals("Required Body [role] not specified", errors);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deleteRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        RolePermission role = createDefaultAdminRole(memberProfile);

        assertNotNull(findRole(role));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(role.getId().toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRole(role));
    }

    @Test
    void deleteRoleNonExisting() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        UUID uuid = UUID.randomUUID();

        assertNull(findRoleById(uuid));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid.toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRoleById(uuid));
    }

    @Test
    void deleteRoleBadId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        RolePermission authRole = createDefaultAdminRole(unrelatedProfile);

        String uuid = "Bill-Nye-The-Science-Guy";

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getPermission().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String errors = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertTrue(errors.contains(String.format("Failed to convert argument [id] for value [%s]", uuid)));
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
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

