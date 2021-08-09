package com.objectcomputing.checkins.services.role;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RoleMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.member.RoleMember;
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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleControllerTest extends TestContainersSuite implements RoleFixture, MemberProfileFixture, RoleMemberFixture {

    @Inject
    @Client("/services/roles")
    HttpClient client;

    @Test
    void testCreateARole() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.ADMIN);
        roleCreateDTO.setDescription("description");
        MemberProfile memberProfile = createADefaultMemberProfile();
        roleCreateDTO.setRoleMembers(List.of(new RoleCreateDTO.RoleMemberCreateDTO(memberProfile.getId(), true)));

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<RoleResponseDTO> response = client.toBlocking().exchange(request, RoleResponseDTO.class);

        RoleResponseDTO roleEntity = response.body();

        assertEquals(roleCreateDTO.getDescription(), roleEntity.getDescription());
        assertEquals(roleCreateDTO.getRole(), roleEntity.getRole());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), roleEntity.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateRoleNoLeads() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRole(RoleType.ADMIN);
        roleCreateDTO.setDescription("description");
        roleCreateDTO.setRoleMembers(new ArrayList<>());

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);

        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Role must include at least one role lead", errors.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), href.asText());
    }

    @Test
    void testCreateAnInvalidRole() {
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("role.role: must not be null", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullRole() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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
    void testCreateARoleWithExistingName() {

        Role roleEntity = createDefaultRole();

        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setDescription("test");
        roleCreateDTO.setRole(roleEntity.getRole());
        roleCreateDTO.setRoleMembers(new ArrayList<>());
        MemberProfile memberProfile = createADefaultMemberProfile();
        roleCreateDTO.setRoleMembers(List.of(new RoleCreateDTO.RoleMemberCreateDTO(memberProfile.getId(), true)));

        final HttpRequest<RoleCreateDTO> request = HttpRequest.POST("", roleCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Role with name %s already exists", roleCreateDTO.getRole()), error);
    }

    @Test
    void testReadRole() {
        Role roleEntity = createDefaultRole() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", roleEntity.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<RoleResponseDTO> response = client.toBlocking().exchange(request, RoleResponseDTO.class);

        assertEntityDTOEqual(roleEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadRoleNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Role.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllRoles() {

        Role roleEntity = createDefaultRole();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RoleResponseDTO.class));

        assertEntityDTOEqual(Set.of(roleEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByName() {
        Role roleEntity = createDefaultRole() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s", roleEntity.getRole())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RoleResponseDTO.class));

        assertEntityDTOEqual(Set.of(roleEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

//    @Test
//    void testFindByMemberId() {
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role roleEntity = createDefaultRole();
//
//        RoleMember roleMemberEntity = createDefaultRoleMember(roleEntity, memberProfile);
//
//        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", roleMemberEntity.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        final HttpResponse<Set<RoleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RoleResponseDTO.class));
//
//        assertEntityDTOEqual(Set.of(roleEntity), response.body());
//        assertEquals(HttpStatus.OK, response.getStatus());
//
//    }
//
//    @Test
//    void testFindRoles() {
//        MemberProfile memberProfile = createADefaultMemberProfile();
//        Role roleEntity = createDefaultRole();
//
//        RoleMember roleMemberEntity = createDefaultRoleMember(roleEntity, memberProfile);
//
//        final HttpRequest<?> request = HttpRequest.GET(String.format("/?role=%s&memberid=%s", roleEntity.getRole(),
//                roleMemberEntity.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        final HttpResponse<Set<RoleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RoleResponseDTO.class));
//
//        assertEntityDTOEqual(Set.of(roleEntity), response.body());
//        assertEquals(HttpStatus.OK, response.getStatus());
//
//    }
//
//    @Test
//    void testUpdatePermissionDenied() {
//        Role roleEntity = createDefaultRole();
//        MemberProfile memberProfile = createADefaultMemberProfile();
//
//        RoleUpdateDTO requestBody = updateFromEntity(roleEntity);
//        RoleUpdateDTO.RoleMemberUpdateDTO newMember = updateDefaultRoleMemberDto(roleEntity, memberProfile, true);
//        newMember.setLead(true);
//        requestBody.setRoleMembers(Collections.singletonList(newMember));
//
//        final HttpRequest<RoleUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
//        assertEquals(request.getPath(), href);
//        assertEquals("You are not authorized to perform this operation", error);
//    }
//
//    @Test
//    void testUpdateRoleSuccess() {
//        MemberProfile user = createAnUnrelatedUser();
//        createDefaultAdminRole(user);
//
//        Role roleEntity = createDefaultRole();
//        MemberProfile memberProfile = createADefaultMemberProfile();
//
//        RoleUpdateDTO requestBody = updateFromEntity(roleEntity);
//        RoleUpdateDTO.RoleMemberUpdateDTO newMember = updateDefaultRoleMemberDto(roleEntity, memberProfile, true);
//        newMember.setLead(true);
//        requestBody.setRoleMembers(Collections.singletonList(newMember));
//
//        final HttpRequest<RoleUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
//        final HttpResponse<RoleResponseDTO> response = client.toBlocking().exchange(request, RoleResponseDTO.class);
//
//        assertEntityDTOEqual(roleEntity, response.body());
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), roleEntity.getId()), response.getHeaders().get("location"));
//    }
//
//    @Test
//    void testUpdateRoleNullName() {
//        Role roleEntity = createDefaultRole();
//
//        RoleUpdateDTO requestBody = new RoleUpdateDTO(roleEntity.getId(), null, null);
//        requestBody.setRoleMembers(new ArrayList<>());
//
//        final HttpRequest<RoleUpdateDTO> request = HttpRequest.PUT("", requestBody)
//                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        JsonNode errors = Objects.requireNonNull(body).get("message");
//        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
//        assertEquals("role.role: must not be blank", errors.asText());
//        assertEquals(request.getPath(), href.asText());
//        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//    }
//
//    @Test
//    void testUpdateANullRole() {
//        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        JsonNode errors = Objects.requireNonNull(body).get("message");
//        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
//        assertEquals("Required Body [role] not specified", errors.asText());
//        assertEquals(request.getPath(), href.asText());
//        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//    }
//
//
//    @Test
//    void testUpdateRoleNotExist() {
//        MemberProfile user = createAnUnrelatedUser();
//        createDefaultAdminRole(user);
//
//        Role roleEntity = createDefaultRole();
//        UUID requestId = UUID.randomUUID();
//        RoleUpdateDTO requestBody = new RoleUpdateDTO(requestId.toString(), roleEntity.getRole(), roleEntity.getDescription());
//        requestBody.setRoleMembers(new ArrayList<>());
//
//        final MutableHttpRequest<RoleUpdateDTO> request = HttpRequest.PUT("", requestBody)
//                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
//                client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(String.format("Role ID %s does not exist, can't update.", requestId), error);
//        assertEquals(request.getPath(), href);
//    }
//
//    @Test
//    void deleteRoleByMember() {
//        // setup role
//        Role roleEntity = createDefaultRole();
//        // create members
//        MemberProfile memberProfileofRoleLeadEntity = createADefaultMemberProfile();
//        MemberProfile memberProfileOfRoleMember = createADefaultMemberProfileForPdl(memberProfileofRoleLeadEntity);
//        //add members to role
//        createLeadRoleMember(roleEntity, memberProfileofRoleLeadEntity);
//        createDefaultRoleMember(roleEntity, memberProfileOfRoleMember);
//
//        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", roleEntity.getId())).basicAuth(memberProfileOfRoleMember.getWorkEmail(), MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        JsonNode errors = Objects.requireNonNull(body).get("message");
//        assertEquals("You are not authorized to perform this operation", errors.asText());
//        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
//    }
//
//    @Test
//    void deleteRoleByAdmin() {
//        // setup role
//        Role roleEntity = createDefaultRole();
//        // create members
//        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
//        createDefaultAdminRole(memberProfileOfAdmin);
//
//        //add members to role
//        createDefaultRoleMember(roleEntity, memberProfileOfAdmin);
//
//        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", roleEntity.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
//        final HttpResponse response = client.toBlocking().exchange(request);
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//    }
//
//    @Test
//    void deleteRoleByRoleLead() {
//        // setup role
//        Role roleEntity = createDefaultRole();
//        // create members
//        MemberProfile memberProfileofRoleLeadEntity = createADefaultMemberProfile();
//        //add members to role
//        createLeadRoleMember(roleEntity, memberProfileofRoleLeadEntity);
//        // createDefaultRoleMember(role, memberProfileOfRoleMember);
//
//        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", roleEntity.getId())).basicAuth(memberProfileofRoleLeadEntity.getWorkEmail(), MEMBER_ROLE);
//        final HttpResponse response = client.toBlocking().exchange(request);
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//    }

    @Test
    void deleteRoleByUnrelatedUser() {
        // setup role
        Role roleEntity = createDefaultRole();
        // create members
        MemberProfile user = createAnUnrelatedUser();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", roleEntity.getId())).basicAuth(user.getWorkEmail(), MEMBER_ROLE);

        //throw error
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    private void assertEntityDTOEqual(Collection<Role> entities, Collection<RoleResponseDTO> dtos) {
        assertEquals(entities.size(), dtos.size());
        Iterator<Role> iEntity = entities.iterator();
        Iterator<RoleResponseDTO> iDTO = dtos.iterator();
        while (iEntity.hasNext() && iDTO.hasNext()) {
            assertEntityDTOEqual(iEntity.next(), iDTO.next());
        }
    }

    private void assertEntityDTOEqual(Role entity, RoleResponseDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.getDescription(), dto.getDescription());
    }
}

