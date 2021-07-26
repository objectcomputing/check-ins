package com.objectcomputing.checkins.services.role_permissions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RolePermissionFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;

class RolePermissionControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture,RolePermissionFixture {

    @Inject
    @Client("/services/permissions")
    HttpClient client;

    ////////////////// CREATE tests///////////////////
    @Test
    void testCreateACheckinRolePermissionbyADMIN() {
        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);

        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRolePermission(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(authRole.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        RolePermission rolepermission = response.body();
        assertNotNull(authRole);
        assertEquals(rolepermissionCreateDTO.getRoleid(), rolepermission.getRoleid());
        assertEquals(rolepermissionCreateDTO.getRolePermission(), rolepermission.getPermission());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateARolePermissionAlreadyExists() {
        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);

        RolePermission alreadyExistingRole = createDefaultRolePermission(RolePermissionType.READCHECKIN, authRole);
        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRolePermission(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(authRole.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Role Id %s already has permission READCHECKIN", alreadyExistingRole.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateARolePermissionbyADMINwithInvalidRoleId() {
        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);

        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRolePermission(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(UUID.randomUUID());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Role %s doesn't exist", rolepermissionCreateDTO.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateARolePermissionByPDLForbidden() {
        MemberProfile memberProfileDefault = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.PDL, memberProfileDefault);

        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRolePermission(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(authRole.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(memberProfileDefault.getWorkEmail(), authRole.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateARolePermissionByMEMBERForbidden() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRolePermission(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(role.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateANullRolePermission() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createDefaultAdminRole(unrelatedProfile);

        final HttpRequest<String> request = HttpRequest.POST("", "")
        .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
        () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [permission] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        }

    @Test
    void createRolePermissionUnauthorized() {

        UUID uuid = UUID.randomUUID();

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role authRole = createDefaultAdminRole(memberProfileOfUser);

        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRolePermission(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(uuid);

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Role %s doesn't exist", rolepermissionCreateDTO.getRoleid()), responseException.getMessage());
    }

    @Test
    void tesCreateRolePermissionWithNullRoleId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role roleProfile = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
        rolepermission.setPermission(null);
        rolepermission.setRoleid(null);

        final HttpRequest<RolePermission> request = HttpRequest.POST("", rolepermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("permission.roleid: must not be null", error);
        assertEquals(request.getPath(), href);
    }

    //////////////// Read tests//////////////////
    @Test
    void testReadRolePermissionByADMIN() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultCheckinRolePermission(role);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", rolepermission.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        assertEquals(rolepermission, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadRolePermissionNotFound() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createDefaultAdminRole(unrelatedProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID()))
        .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, RolePermission.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        }

    @Test
    void testReadRolePermissionByMemberForbidden() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultCheckinRolePermission(role);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", rolepermission.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testReadRolePermissionWithInvalidRoleId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultCheckinRolePermission(role);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("No role item for UUID", rolepermission.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testFindRolePermissionByPermission() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.CREATECHECKIN,role);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?permission=%s&roleid=%s", rolepermission.getPermission(),
                "")).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<Set<RolePermission>> response = client.toBlocking().exchange(request, Argument.setOf(RolePermission.class));

        assertEquals(Set.of(rolepermission), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        }

    @Test
    void testFindRolePermissionByRoleId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.CREATECHECKIN,role);


        final HttpRequest<?> request = HttpRequest.GET(String.format("/?permission=%s&roleid=%s", "",
                rolepermission.getRoleid())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<Set<RolePermission>> response = client.toBlocking().exchange(request, Argument.setOf(RolePermission.class));

        assertEquals(Set.of(rolepermission), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRolePermissionsAllParams() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.CREATECHECKIN,role);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?permission=%s&roleid=%s", "",
                "")).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<Set<RolePermission>> response = client.toBlocking().exchange(request, Argument.setOf(RolePermission.class));

        assertEquals(Set.of(rolepermission), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRolePermissionsDoesNotExist() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultCheckinRolePermission(role);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/?permission=%s&roleid=%s", rolepermission.getPermission(),rolepermission.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testFindRolePermission() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role role = createDefaultAdminRole(unrelatedProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?id=%s", RolePermissionType.CREATECHECKIN))
        .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
        HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(), response.body());
        }

    ////////////////// UPDATE tests///////////////////
    @Test
    void testUpdateRolePermission() {

        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission updateExistingRolePermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", updateExistingRolePermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        assertEquals(updateExistingRolePermission, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), updateExistingRolePermission.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateRolePermissionWithNonExistingRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);

        rolepermission.setRoleid(UUID.randomUUID());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Role Id %s doesn't exist", rolepermission.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingRolePermissionType() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role roleToUpdate = createDefaultAdminRole(memberProfile);

        Map<String, String> rolepermission = new HashMap<>();
        rolepermission.put("id", roleToUpdate.getId().toString());
        rolepermission.put("permission", "ROLE_DOES_NOT_EXIST");
        rolepermission.put("roleid", roleToUpdate.getMemberid().toString());

        final HttpRequest<Map<String, String>> request = HttpRequest.PUT("", rolepermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertTrue(error.contains("not one of the values accepted for Enum class"));
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingRolePermission() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role roleProfile = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = new RolePermission(UUID.randomUUID(), RolePermissionType.READCHECKIN, roleProfile.getId());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate permission to update with id %s", rolepermission.getId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateRolePermissionWithoutRoleId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role roleProfile = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
        rolepermission.setId(null);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("Unable to locate permission to update with id null", error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateForbidden() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);

        RolePermission rolepermission = new RolePermission(UUID.randomUUID(), RolePermissionType.READCHECKIN, authRole.getId());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
        .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
        client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateAnInvalidRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role roleProfile = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = createDefaultCheckinRolePermission(roleProfile);
        rolepermission.setRoleid(null);
        rolepermission.setPermission(null);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
            .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
            HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
        .stream().sorted().collect(Collectors.toList());
        assertEquals("permission.permission: must not be null", errorList.get(0));
        assertEquals("permission.roleid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        }


    @Test
    void testUpdateRolePermissionWithNullRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role roleProfile = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
        rolepermission.setPermission(null);

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("permission.permission: must not be null", error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateRolePermissionWithANullPermission() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        final HttpRequest<String> request = HttpRequest.PUT("", "")
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String errors = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertEquals("Required Body [permission] not specified", errors);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    ///////////////  DELETE tests ////////////////
    @Test
    void deleteRolePermission() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);

        assertNotNull(findRolePermission(rolepermission));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(rolepermission.getId().toString())
                .basicAuth(memberProfile.getWorkEmail(), role.getRole().name());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRolePermission(rolepermission));
    }

    @Test
    void deleteRolePermissionForbidden() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);
        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);

        assertNotNull(findRolePermission(rolepermission));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(rolepermission.getId().toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void deleteRoleNonExisting() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        UUID uuid = UUID.randomUUID();

        assertNull(findRoleById(uuid));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid.toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRoleById(uuid));
    }

    @Test
    void deleteRolePermissionBadId() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        String uuid = "Bill-Nye-The-Science-Guy";

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
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
    void deleteRolePermisisonUnauthorized() {
        UUID uuid = UUID.randomUUID();

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }
}