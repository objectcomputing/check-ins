package com.objectcomputing.checkins.services.role_permissions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RolePermissionFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
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

import static org.junit.jupiter.api.Assertions.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;

class RolePermissionControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture,RolePermissionFixture {

    @Inject
    @Client("/services/permissions")
    HttpClient client;

    @Test
    void createRolePermissionUnauthorized() {

        UUID uuid = UUID.randomUUID();

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = createDefaultRole(RoleType.ADMIN, memberProfileOfPDL);
        Role authRole = createDefaultAdminRole(memberProfileOfUser);

        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
        roleCreateDTO.setRoleid(uuid);

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Role %s doesn't exist", roleCreateDTO.getRoleid()), responseException.getMessage());
    }

    @Test
    void testCreateACheckinRolePermissionbyADMIN() {
            MemberProfile memberProfileOfUser = createADefaultMemberProfile();
            Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);

            RolePermission rolePermission = createDefaultCheckinRolePermission(authRole);

            RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
            roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
            roleCreateDTO.setRoleid(authRole.getId());

            final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                    .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
            final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

            RolePermission rolepermission = response.body();
            assertNotNull(authRole);
            assertEquals(roleCreateDTO.getRoleid(), rolepermission.getRoleid());
            assertEquals(roleCreateDTO.getRole(), rolepermission.getPermission());
            assertEquals(HttpStatus.CREATED, response.getStatus());
            assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
        }
//        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
//        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);
//
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        roleCreateDTO.setRoleid(authRole.getId());
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
//                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
//        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
//
//        RolePermission rolepermission = response.body();
//        assertNotNull(authRole);
//        assertEquals(roleCreateDTO.getRoleid(), rolepermission.getRoleid());
//        assertEquals(roleCreateDTO.getRole(), rolepermission.getPermission());
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));


    @Test
    void testCreateADuplicateRolePermissionByADMIN() {
        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);

        RolePermission alreadyExistingRole = createDefaultRolePermission(RolePermissionType.READCHECKIN, authRole);
        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
        roleCreateDTO.setRoleid(authRole.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Role %s already has permission READCHECKIN", alreadyExistingRole.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateARolePermissionbyADMINwithInvalidRoleId() {
        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);

        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
        roleCreateDTO.setRoleid(UUID.randomUUID());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Role %s doesn't exist", roleCreateDTO.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateARolePermissionWithPDLForbidden() {
        MemberProfile memberProfileDefault = createADefaultMemberProfile();
        Role authRole = createDefaultRole(RoleType.PDL, memberProfileDefault);

        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
        roleCreateDTO.setRoleid(authRole.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
                .basicAuth(memberProfileDefault.getWorkEmail(), authRole.getRole().name());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateARolePermissionWithMEMBERForbidden() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
        rolepermissionCreateDTO.setRole(RolePermissionType.READCHECKIN);
        rolepermissionCreateDTO.setRoleid(role.getId());

        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    //////////////// Read //////////////////

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

    ////////////////// UPDATE ///////////////////
    @Test
    void testUpdateRolePermission() {

        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission updateExistingRole = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);
        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
        roleCreateDTO.setRole(RolePermissionType.DELETECHECKIN);
        roleCreateDTO.setRoleid(authRole.getId());

        final HttpRequest<RolePermission> request = HttpRequest.PUT("", updateExistingRole)
                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        assertEquals(updateExistingRole, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), updateExistingRole.getId()), response.getHeaders().get("location"));
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

        assertEquals(String.format("Role %s doesn't exist", rolepermission.getRoleid()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingRoleType() {
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

    ///////////////  DELETE  ////////////////
    @Test
    void deleteRole() {
        MemberProfile unrelatedProfile = createAnUnrelatedUser();
        Role authRole = createDefaultAdminRole(unrelatedProfile);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createDefaultAdminRole(memberProfile);

        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);

        assertNotNull(findRolePermission(rolepermission));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(rolepermission.getId().toString())
                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(findRolePermission(rolepermission));
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
    void deleteRoleBadId() {
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
    void deleteRoleUnauthorized() {
        UUID uuid = UUID.randomUUID();

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

}









//////////////////////////////////////////////////////


//
//    @Test
//    void testUpdateForbidden() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);
//
//        Role roleProfile = createDefaultAdminRole(unrelatedProfile);
//
////        RolePermission r = new RolePermission(UUID.randomUUID(), RolePermissionType.READCHECKIN, UUID.randomUUID());
//
//        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
////        rolepermission.setId(null);
//
//        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//
//
////        final HttpRequest<RolePermission> request = HttpRequest.PUT("", r)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, String.class));
//
//        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
//        assertEquals("Forbidden", responseException.getMessage());
//    }















//
//
//
//////////////////////////////////////////////////////////////////////////////////////////////////////
////    @Test
////    void testCreateARolePermission() {
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
//////        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
//////        roleCreateDTO.setRole(RoleType.MEMBER);
//////        roleCreateDTO.setMemberid(memberProfile.getId());
////
////        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
////        rolepermissionCreateDTO.setRole(RolePermissionType.READCHECKIN);
////        rolepermissionCreateDTO.setRoleid(authRole.getId());
////
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);
////
////        Role rolepermission = response.body();
////        assertNotNull(rolepermission);
////        assertEquals(rolepermissionCreateDTO.getRoleid(), rolepermission.getMemberid());
////        assertEquals(rolepermissionCreateDTO.getRole(), rolepermission.getRole());
////        assertEquals(HttpStatus.CREATED, response.getStatus());
////        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
////    }
//
//    @Test
//    void createRolePermissionUnauthorized() {
//
//        UUID uuid = UUID.randomUUID();
//
//        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
//        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
//        Role role = createDefaultRole(RoleType.ADMIN, memberProfileOfPDL);
//        Role authRole = createDefaultAdminRole(memberProfileOfUser);
//
////        RolePermission rolePermission = createDefaultCheckinCreatePermission(role);
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        createDefaultRole(RoleType.ADMIN, memberProfile);
////        MemberProfile permissionProfile = createAnUnrelatedUser();
//
//
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        roleCreateDTO.setRoleid(uuid);
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
//                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
//
////        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, String.class));
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
////        assertEquals("Unauthorized", responseException.getMessage());
//    }
//
//
////    @Test
////    void testCreateARolePermission() {
//////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile memberProfile = createADefaultMemberProfile();
//////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(memberProfile);
////
////        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
////        rolepermissionCreateDTO.setRole(RolePermissionType.READCHECKIN);
////        rolepermissionCreateDTO.setRoleid(memberProfile.getId());
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
////                .basicAuth(memberProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);
////
////        Role rolepermission = response.body();
////        assertNotNull(rolepermission);
////        assertEquals(rolepermissionCreateDTO.getRoleid(), rolepermission.getMemberid());
////        assertEquals(rolepermissionCreateDTO.getRole(), rolepermission.getRole());
////        assertEquals(HttpStatus.CREATED, response.getStatus());
////        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
////    }
//
//
////    @Test
////    void testCreateARolePermissionADMIN() {
////        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
//////        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
////        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);
//////        Role role = createDefaultRole(RoleType.PDL, memberProfileOfUser);
////
//////        RolePermission rolePermission = createDefaultCheckinCreatePermission(role);
//////        MemberProfile memberProfile = createADefaultMemberProfile();
//////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//////        createDefaultRole(RoleType.ADMIN, memberProfile);
//////        MemberProfile permissionProfile = createAnUnrelatedUser();
////
////
////        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
////        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
////        roleCreateDTO.setRoleid(authRole.getId());
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
////                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
////
////        RolePermission rolepermission = response.body();
////        assertNotNull(authRole);
////        assertEquals(roleCreateDTO.getRoleid(), rolepermission.getRoleid());
////        assertEquals(roleCreateDTO.getRole(), rolepermission.getPermission());
////        assertEquals(HttpStatus.CREATED, response.getStatus());
////        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
////    }
//
//
//    @Test
//    void testCreateACheckinRolePermissionbyADMIN() {
//        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
////        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
//        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);
////        Role role = createDefaultRole(RoleType.PDL, memberProfileOfUser);
//
//        RolePermission rolePermission = createDefaultCheckinRolePermission(authRole);
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        createDefaultRole(RoleType.ADMIN, memberProfile);
////        MemberProfile permissionProfile = createAnUnrelatedUser();
//
//
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        roleCreateDTO.setRoleid(authRole.getId());
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
//                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
//        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
//
//        RolePermission rolepermission = response.body();
//        assertNotNull(authRole);
//        assertEquals(roleCreateDTO.getRoleid(), rolepermission.getRoleid());
//        assertEquals(roleCreateDTO.getRole(), rolepermission.getPermission());
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
//    }
//
//    @Test
//    void testCreateACheckinRolePermissionbyADMINAlready() {
//        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
////        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
//        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);
////        Role role = createDefaultRole(RoleType.PDL, memberProfileOfUser);
//
//        RolePermission rolePermission = createDefaultCheckinRolePermission(authRole);
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        createDefaultRole(RoleType.ADMIN, memberProfile);
////        MemberProfile permissionProfile = createAnUnrelatedUser();
//
//        RolePermission alreadyExistingRole = createDefaultRolePermission(RolePermissionType.READCHECKIN, authRole);
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        roleCreateDTO.setRoleid(authRole.getId());
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
//                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(String.format("Role %s already has permission READCHECKIN", alreadyExistingRole.getRoleid()), error);
//        assertEquals(request.getPath(), href);
////        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
////
////        RolePermission rolepermission = response.body();
////        assertNotNull(authRole);
////        assertEquals(roleCreateDTO.getRoleid(), rolepermission.getRoleid());
////        assertEquals(roleCreateDTO.getRole(), rolepermission.getPermission());
////        assertEquals(HttpStatus.CREATED, response.getStatus());
////        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
//    }
//
//    @Test
//    void testCreateACheckinRolePermissionbyADMINAlUUIDrandom() {
//        MemberProfile memberProfileOfUser = createADefaultMemberProfile();
////        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
//        Role authRole = createDefaultRole(RoleType.ADMIN, memberProfileOfUser);
////        Role role = createDefaultRole(RoleType.PDL, memberProfileOfUser);
//
////        RolePermission rolePermission = createDefaultCheckinRolePermission(authRole);
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        createDefaultRole(RoleType.ADMIN, memberProfile);
////        MemberProfile permissionProfile = createAnUnrelatedUser();
//
//        RolePermission alreadyExistingRole = createDefaultRolePermission(RolePermissionType.READCHECKIN, authRole);
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        roleCreateDTO.setRoleid(UUID.randomUUID());
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
//                .basicAuth(memberProfileOfUser.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(String.format("Role %s doesn't exist", roleCreateDTO.getRoleid()), error);
//        assertEquals(request.getPath(), href);
////        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
////
////        RolePermission rolepermission = response.body();
////        assertNotNull(authRole);
////        assertEquals(roleCreateDTO.getRoleid(), rolepermission.getRoleid());
////        assertEquals(roleCreateDTO.getRole(), rolepermission.getPermission());
////        assertEquals(HttpStatus.CREATED, response.getStatus());
////        assertEquals(String.format("%s/%s", request.getPath(), rolepermission.getId()), response.getHeaders().get("location"));
//    }
//
//    @Test
//    void testCreateARolePermissionPDL() {
//        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
//        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
//        Role authRole = createDefaultRole(RoleType.PDL, memberProfileOfPDL);
//
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        roleCreateDTO.setRoleid(authRole.getId());
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
//                .basicAuth(memberProfileOfPDL.getWorkEmail(), authRole.getRole().name());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, String.class));
//        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
//        assertEquals("Forbidden", responseException.getMessage());
//    }
//
//    @Test
//    void testCreateARolePermissionMEMBER() {
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermissionCreateDTO rolepermissionCreateDTO = new RolePermissionCreateDTO();
//        rolepermissionCreateDTO.setRole(RolePermissionType.READCHECKIN);
//        rolepermissionCreateDTO.setRoleid(role.getId());
//
//        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", rolepermissionCreateDTO)
//                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, String.class));
//        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
//        assertEquals("Forbidden", responseException.getMessage());
//    }
//
//
//
//
////    @Test
////    void testCreateARoleAlreadyExists() {
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role role = createDefaultAdminRole(unrelatedProfile);
////
////        Role alreadyExistingRole = createDefaultRole(RolePermissionType.READCHECKIN, memberProfile);
////        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
////        roleCreateDTO.setRole(alreadyExistingRole.getRole());
////        roleCreateDTO.setMemberid(alreadyExistingRole.getMemberid());
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
////                client.toBlocking().exchange(request, Map.class));
////
////        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
////        String error = Objects.requireNonNull(body).get("message").asText();
////        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
////
////        assertEquals(String.format("Member %s already has role %s", roleCreateDTO.getMemberid(), roleCreateDTO.getRole()),
////                error);
////        assertEquals(request.getPath(), href);
////    }
////
////    @Test
////    void testCreateForbidden() {
////
////
////        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
////        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
////        Role role = createDefaultRole(RoleType.MEMBER, memberProfileOfPDL);
////        Role authRole = createDefaultAdminRole(memberProfileOfUser);
////
////        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
////        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
////        roleCreateDTO.setRoleid(role.getId());
////
////
////        RolePermission rolepermnission = createDefaultCheckinCreatePermission(role);
////
////        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
////        roleCreateDTO.setRole(RolePermissionType.READCHECKIN);
////        roleCreateDTO.setRoleid(UUID.randomUUID());
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
////                client.toBlocking().exchange(request, String.class));
////
////        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
////        assertEquals("Forbidden", responseException.getMessage());
////    }
////
////    @Test
////    void testCreateAnInvalidRole() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role role = createDefaultAdminRole(unrelatedProfile);
////
////        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", roleCreateDTO)
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
////                () -> client.toBlocking().exchange(request, Map.class));
////
////        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
////        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
////        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
////        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
////                .stream().sorted().collect(Collectors.toList());
////        assertEquals("role.memberid: must not be null", errorList.get(0));
////        assertEquals("role.role: must not be null", errorList.get(1));
////        assertEquals(request.getPath(), href.asText());
////        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
////    }
////
////    @Test
////    void testCreateNonExistingMember() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
////        RolePermissionCreateDTO role = new RolePermissionCreateDTO();
////        role.setMemberid(UUID.randomUUID());
////        role.setRole(RolePermissionType.READCHECKIN);
////
////
////        final HttpRequest<RolePermissionCreateDTO> request = HttpRequest.POST("", role)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
////                client.toBlocking().exchange(request, Map.class));
////
////        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
////        String error = Objects.requireNonNull(body).get("message").asText();
////        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
////
////        assertEquals(String.format("Member %s doesn't exist", role.getMemberid()), error);
////        assertEquals(request.getPath(), href);
////    }
////
////    @Test
////    void testCreateANullRole() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role role = createDefaultAdminRole(unrelatedProfile);
////
////        final HttpRequest<String> request = HttpRequest.POST("", "")
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
////                () -> client.toBlocking().exchange(request, Map.class));
////
////        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
////        JsonNode errors = Objects.requireNonNull(body).get("message");
////        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
////        assertEquals("Required Body [role] not specified", errors.asText());
////        assertEquals(request.getPath(), href.asText());
////        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
////    }
//
//    @Test
//    void testReadRolePermission() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermission rolepermission = createDefaultCheckinRolePermission(role);
//
//        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", rolepermission.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
//
//        assertEquals(rolepermission, response.body());
//        assertEquals(HttpStatus.OK, response.getStatus());
//    }
//
//    @Test
//    void testReadRolePermissionForbidden() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermission rolepermission = createDefaultCheckinRolePermission(role);
//
//        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", rolepermission.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, String.class));
//        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
//        assertEquals("Forbidden", responseException.getMessage());
//    }
//
//
//
//
////    @Test
////    void testReadRole() {
////
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Role role = createDefaultAdminRole(memberProfile);
////
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultRole(RolePermissionType.READCHECKIN, unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Role role = createDefaultAdminRole(memberProfile);
////
////        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", role.getId())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);
////
////        assertEquals(role, response.body());
////        assertEquals(HttpStatus.OK, response.getStatus());
////    }
////
////    @Test
////    void testReadRoleNotFound() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role role = createDefaultAdminRole(unrelatedProfile);
////
////        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID()))
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Role.class));
////
////        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
////    }
//
//    @Test
//    void testReadRolePermissionNotFound() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermission rolepermission = createDefaultCheckinRolePermission(role);
//
//        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(String.format("No role item for UUID", rolepermission.getRoleid()), error);
//        assertEquals(request.getPath(), href);
//    }
////
////    @Test
////    void testFindRoles() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role role = createDefaultAdminRole(unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Set<Role> roles = Set.of(createDefaultRole(RolePermissionType.ADMIN, memberProfile),
////                createDefaultRole(RolePermissionType.PDL, memberProfile));
////
////        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", memberProfile.getId()))
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        final HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));
////
////        assertEquals(roles, response.body());
////        assertEquals(HttpStatus.OK, response.getStatus());
////    }
////
////    @Test
////    void testFindRolesAllParams() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Role role = createDefaultAdminRole(memberProfile);
////
////        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", role.getRole(),
////                role.getMemberid())).basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));
////
////        assertEquals(Set.of(role), response.body());
////        assertEquals(HttpStatus.OK, response.getStatus());
////    }
////
////    @Test
////    void testFindRolesDoesNotExist() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role role = createDefaultAdminRole(unrelatedProfile);
////
////        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s", RolePermissionType.PDL))
////                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
////        HttpResponse<Set<Role>> response = client.toBlocking().exchange(request, Argument.setOf(Role.class));
////
////        assertEquals(HttpStatus.OK, response.getStatus());
////        assertEquals(Set.of(), response.body());
////    }
////
////    @Test
////    void testUpdateRole() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Role role = createDefaultAdminRole(memberProfile);
////
////
////
////        final HttpRequest<Role> request = HttpRequest.PUT("", role)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpResponse<Role> response = client.toBlocking().exchange(request, Role.class);
////
////        assertEquals(role, response.body());
////        assertEquals(HttpStatus.OK, response.getStatus());
////        assertEquals(String.format("%s/%s", request.getPath(), role.getId()), response.getHeaders().get("location"));
////    }
//
//
//    @Test
//    void testUpdateRole2() {
//
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermission updateExistingRole = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);
//        RolePermissionCreateDTO roleCreateDTO = new RolePermissionCreateDTO();
//        roleCreateDTO.setRole(RolePermissionType.DELETECHECKIN);
//        roleCreateDTO.setRoleid(authRole.getId());
//
//        final HttpRequest<RolePermission> request = HttpRequest.PUT("", updateExistingRole)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);
//
//        assertEquals(updateExistingRole, response.body());
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), updateExistingRole.getId()), response.getHeaders().get("location"));
//    }
//
//
//    @Test
//    void testUpdateNonExistingMember() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultRole(RoleType.ADMIN, unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Role role = createDefaultAdminRole(memberProfile);
//
//        rolepermission.setRoleid(UUID.randomUUID());
//
//        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(String.format("Role %s doesn't exist", rolepermission.getRoleid()), error);
//        assertEquals(request.getPath(), href);
//    }
//
//    @Test
//    void testUpdateNonExistingRoleType() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role roleToUpdate = createDefaultAdminRole(memberProfile);
//
//
////        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleToUpdate);
//        Map<String, String> rolepermission = new HashMap<>();
//        rolepermission.put("id", roleToUpdate.getId().toString());
//        rolepermission.put("permission", "ROLE_DOES_NOT_EXIST");
//        rolepermission.put("roleid", roleToUpdate.getMemberid().toString());
//
//        final HttpRequest<Map<String, String>> request = HttpRequest.PUT("", rolepermission)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertTrue(error.contains("not one of the values accepted for Enum class"));
//        assertEquals(request.getPath(), href);
//    }
//
//    @Test
//    void testUpdateNonExistingRole() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role roleProfile = createDefaultAdminRole(memberProfile);
//        RolePermission rolepermission = new RolePermission(UUID.randomUUID(), RolePermissionType.READCHECKIN, roleProfile.getId());
//
//        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(String.format("Unable to locate permission to update with id %s", rolepermission.getId()), error);
//        assertEquals(request.getPath(), href);
//    }
//
//    @Test
//    void testUpdateWithoutId() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role roleProfile = createDefaultAdminRole(memberProfile);
//        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
//        rolepermission.setId(null);
//
//        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals("Unable to locate permission to update with id null", error);
//        assertEquals(request.getPath(), href);
//    }
//
//
//
//
////    @Test
////    void testUpdateForbidden2() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultRole(RolePermissionType.READCHECKIN, unrelatedProfile);
////
////        Role r = new Role(UUID.randomUUID(), RolePermissionType.ADMIN, UUID.randomUUID());
////
////        final HttpRequest<Role> request = HttpRequest.PUT("", r)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
////                client.toBlocking().exchange(request, String.class));
////
////        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
////        assertEquals("Forbidden", responseException.getMessage());
////    }
////
////    @Test
////    void testUpdateAnInvalidRole() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////
////        Role roleProfile = createDefaultAdminRole(memberProfile);
////        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
////
////
//////        Role role = createDefaultAdminRole(memberProfile);
////        rolepermission.setId(null);
////        rolepermission.setRoleid(null);
////
////        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
////                () -> client.toBlocking().exchange(request, Map.class));
////
////        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
////        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
////        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
////        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
////                .stream().sorted().collect(Collectors.toList());
////        assertEquals("role.memberid: must not be null", errorList.get(0));
////        assertEquals("role.role: must not be null", errorList.get(1));
////        assertEquals(request.getPath(), href.asText());
////        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
////    }
//
//    @Test
//    void testUpdateInvalidRole2() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role roleProfile = createDefaultAdminRole(memberProfile);
//        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
//        rolepermission.setPermission(null);
//
//        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals("permission.permission: must not be null", error);
//        assertEquals(request.getPath(), href);
//    }
//
////    @Test
////    void testUpdateInvalidRole3() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultAdminRole(unrelatedProfile);
////
////        MemberProfile memberProfile = createADefaultMemberProfile();
////        Role roleProfile = createDefaultAdminRole(memberProfile);
////        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
////        rolepermission.setPermission(RolePermissionType."EDITCHECKIN");
////
////        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
////                client.toBlocking().exchange(request, Map.class));
////
////        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
////        String error = Objects.requireNonNull(body).get("message").asText();
////        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
////
////        assertEquals("permission.permission: must not be null", error);
////        assertEquals(request.getPath(), href);
////    }
////
//    @Test
//    void testUpdateANullRole() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        final HttpRequest<String> request = HttpRequest.PUT("", "")
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String errors = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//        assertEquals("Required Body [permission] not specified", errors);
//        assertEquals(request.getPath(), href);
//        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//    }
////
//    @Test
//    void deleteRole() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role role = createDefaultAdminRole(memberProfile);
//
//        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, role);
//
//        assertNotNull(findRolePermission(rolepermission));
//
//        final MutableHttpRequest<Object> request = HttpRequest.DELETE(rolepermission.getId().toString())
//                .basicAuth(unrelatedProfile.getWorkEmail(), role.getRole().name());
//        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertNull(findRolePermission(rolepermission));
//    }
//
//    @Test
//    void deleteRoleNonExisting() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        UUID uuid = UUID.randomUUID();
//
//        assertNull(findRoleById(uuid));
//
//        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid.toString())
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertNull(findRoleById(uuid));
//    }
//
//    @Test
//    void deleteRoleBadId() {
//        MemberProfile unrelatedProfile = createAnUnrelatedUser();
//        Role authRole = createDefaultAdminRole(unrelatedProfile);
//
//        String uuid = "Bill-Nye-The-Science-Guy";
//
//        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid)
//                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String errors = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//        assertTrue(errors.contains(String.format("Failed to convert argument [id] for value [%s]", uuid)));
//        assertEquals(request.getPath(), href);
//        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//    }
//
//    @Test
//    void deleteRoleUnauthorized() {
//        UUID uuid = UUID.randomUUID();
//
//        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, String.class));
//
//        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
//        assertEquals("Unauthorized", responseException.getMessage());
//    }
//}
//
//
//
//
//
////////////////////////////////////////////////////////
//
//
////
////    @Test
////    void testUpdateForbidden() {
////        MemberProfile unrelatedProfile = createAnUnrelatedUser();
////        Role authRole = createDefaultRole(RoleType.MEMBER, unrelatedProfile);
////
////        Role roleProfile = createDefaultAdminRole(unrelatedProfile);
////
//////        RolePermission r = new RolePermission(UUID.randomUUID(), RolePermissionType.READCHECKIN, UUID.randomUUID());
////
////        RolePermission rolepermission = createDefaultRolePermission(RolePermissionType.READCHECKIN, roleProfile);
//////        rolepermission.setId(null);
////
////        final HttpRequest<RolePermission> request = HttpRequest.PUT("", rolepermission)
////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////
////
//////        final HttpRequest<RolePermission> request = HttpRequest.PUT("", r)
//////                .basicAuth(unrelatedProfile.getWorkEmail(), authRole.getRole().name());
////        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
////                client.toBlocking().exchange(request, String.class));
////
////        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
////        assertEquals("Forbidden", responseException.getMessage());
////    }
