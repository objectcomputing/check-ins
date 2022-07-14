package com.objectcomputing.checkins.services.role;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberRoleFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleId;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RoleControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, MemberRoleFixture {

    @Inject
    @Client("/services/roles")
    HttpClient client;

    @Test
      void testCreateARole() {
        createAndAssignRoles();
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = assignAdminRole(unrelatedProfile);

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole("TEST.ROLE");

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        Role role = response.body();
        assertNotNull(role);
        assertEquals(roleCreateDTO.getRole(), role.getRole());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), role.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateForbidden() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.MEMBER, unrelatedProfile);

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.MEMBER.name());


        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateAnInvalidRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignAdminRole(unrelatedProfile);

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("role.role: must not be null", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void createRoleOfSameNameAsExistingRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignAdminRole(unrelatedProfile);

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole("ADMIN");


        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Role with name ADMIN already exists in database", body.get("message").asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void createRoleOfSameNameCaseInsensitiveThrowsError() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignAdminRole(unrelatedProfile);

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole("admin");


        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Role with name admin already exists in database", body.get("message").asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testCreateANullRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignAdminRole(unrelatedProfile);

        final HttpRequest<String> request = HttpRequest.POST("", "")
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [role] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testReadRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignRole(RoleType.MEMBER, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createAndAssignAdminRole(memberProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", role.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(role, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadRoleNotFound() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignAdminRole(unrelatedProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Role.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }



    @Test
    void testFindRoleDoesNotExist() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createAndAssignAdminRole(unrelatedProfile);

        final HttpRequest<?> request = HttpRequest.GET("/" + UUID.randomUUID())
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole());

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Role.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testUpdateRole() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Role newRole = createRole(new Role("Test", "description"));
        newRole.setRole("New name");
        newRole.setDescription("New description");

        final HttpRequest<Role> request = HttpRequest.PUT("", newRole)
                .basicAuth(memberProfile.getWorkEmail(), "ADMIN");
        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);

        assertEquals(newRole, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), newRole.getId()), response.getHeaders().get("location"));
    }


    @Test
    void testUpdateNonExistingRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        Role role = new Role(UUID.randomUUID(), RoleType.MEMBER.name(), "role description");

        final HttpRequest<Role> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
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
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfile);
        role.setId(null);

        final HttpRequest<Role> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
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
        Role authRole = createAndAssignRole(RoleType.MEMBER, unrelatedProfile);

        Role r = new Role(UUID.randomUUID(), RoleType.ADMIN.name(), "role description");

        final HttpRequest<Role> request = HttpRequest.PUT("", r)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateAnInvalidRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        Role role = createRole(new Role("Sample Name", "Sample Description"));
        role.setRole(null);

        final HttpRequest<Role> request = HttpRequest.PUT("", role)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("role.role: must not be null", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        final HttpRequest<String> request = HttpRequest.PUT("", "")
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertEquals("Required Body [role] not specified", error.asText());
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deleteRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        Role role = createRole(new Role ("name", "description"));

        assertNotNull(findRole(role));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(role.getId().toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRole(role));
    }

    @Test
    void deleteRoleWithUsersAssigned() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        MemberProfile member = createADefaultMemberProfile();
        Role role = createRole(new Role ("name", "description"));

        assignMemberToRole(member, role);
        assertNotNull(findRole(role));
        assertTrue(findMemberRole(new MemberRoleId(member.getId(), role.getId())).isPresent());

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(role.getId().toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRole(role));
    }



    @Test
    void deleteRoleNonExisting() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        UUID uuid = UUID.randomUUID();

        assertNull(findRoleById(uuid));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid.toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRoleById(uuid));
    }

    @Test
    void deleteRoleBadId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createAndAssignAdminRole(unrelatedProfile);

        String uuid = "Bill-Nye-The-Science-Guy";

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertTrue(error.asText().contains(String.format("Failed to convert argument [id] for value [%s]", uuid)));
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