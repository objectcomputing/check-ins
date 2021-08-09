package com.objectcomputing.checkins.services.role.member;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RoleMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
//import com.objectcomputing.checkins.services.role.member.MemberHistory;
import com.objectcomputing.checkins.services.role.member.RoleMember;
import com.objectcomputing.checkins.services.role.member.RoleMemberCreateDTO;
import com.objectcomputing.checkins.services.role.member.RoleMemberUpdateDTO;
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

class RoleMemberControllerTest extends TestContainersSuite implements RoleFixture, MemberProfileFixture, RoleMemberFixture {

    @Inject
    @Client("/services/roles/members")
    HttpClient client;

    @Test
    void testCreateARoleMemberByAdmin() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMemberCreateDTO roleMemberCreateDTO = new RoleMemberCreateDTO(role.getId(), memberProfile.getId(), false);
        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", roleMemberCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        RoleMember roleMember = response.body();

        assertEquals(roleMemberCreateDTO.getMemberId(), roleMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), roleMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateARoleMemberByRoleLead() {
        Role role = createDefaultRole();

        // Create a role lead and add him to the role
        MemberProfile memberProfileOfRoleLead = createADefaultMemberProfile();
//        createLeadRoleMember(role, memberProfileOfRoleLead);

        // Create a member and add him to role
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        RoleMemberCreateDTO roleMemberCreateDTO = new RoleMemberCreateDTO(role.getId(), memberProfileOfUser.getId(), false);
        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", roleMemberCreateDTO).basicAuth(memberProfileOfRoleLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        RoleMember roleMember = response.body();

        assertEquals(roleMemberCreateDTO.getMemberId(), roleMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), roleMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateARoleMemberThrowsExceptionForNotAdminAndNotRoleLead() {
        Role role = createDefaultRole();

        // Create a user (not role lead) and add him to the role
        MemberProfile memberProfileOfRolemate = createADefaultMemberProfile();
//        createLeadRoleMember(role, memberProfileOfRolemate);

        // Create a member and add him to role
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        RoleMemberCreateDTO roleMemberCreateDTO = new RoleMemberCreateDTO(role.getId(), memberProfileOfUser.getId(), false);
        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", roleMemberCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAnInvalidRoleMember() {
        RoleMemberCreateDTO dto = new RoleMemberCreateDTO(null, null, null);

        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("roleMember.memberId: must not be null", errorList.get(0));
        assertEquals("roleMember.roleId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateANullRoleMember() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [roleMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateARoleMemberWithNonExistingRole() {

        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMemberCreateDTO roleMemberResponseDTO = new RoleMemberCreateDTO(UUID.randomUUID(), memberProfile.getId(), false);

        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", roleMemberResponseDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Role %s doesn't exist", roleMemberResponseDTO.getRoleId()), error);
    }

    @Test
    void testCreateARoleMemberWithNonExistingMember() {

        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMemberCreateDTO requestDTO = new RoleMemberCreateDTO(role.getId(), UUID.randomUUID(), false);

        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", requestDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", requestDTO.getMemberId()), error);
    }

    @Test
    void testCreateARoleMemberWithExistingMemberAndRole() {

        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        RoleMemberCreateDTO roleMemberResponseDTO = new RoleMemberCreateDTO(roleMember.getRoleId(), memberProfile.getId(), false);

        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", roleMemberResponseDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s already exists in role %s", roleMemberResponseDTO.getMemberId(), roleMemberResponseDTO.getRoleId()), error);
    }

    @Test
    void testReadRoleMember() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", roleMember.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        assertEquals(roleMember, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadRoleMemberNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, RoleMember.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllRoleMembers() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleMember>> response = client.toBlocking().exchange(request, Argument.setOf(RoleMember.class));

        assertEquals(Set.of(roleMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByRoleId() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?roleid=%s", roleMember.getRoleId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleMember>> response = client.toBlocking().exchange(request, Argument.setOf(RoleMember.class));

        assertEquals(Set.of(roleMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberId=%s", roleMember.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleMember>> response = client.toBlocking().exchange(request, Argument.setOf(RoleMember.class));

        assertEquals(Set.of(roleMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRoleMembers() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?roleId=%s&memberId=%s", roleMember.getRoleId(),
                roleMember.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleMember>> response = client.toBlocking().exchange(request, Argument.setOf(RoleMember.class));

        assertEquals(Set.of(roleMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindRoleMembersAllParams() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?roleId=%s&memberId=%s&lead=%s", roleMember.getRoleId(),
                roleMember.getMemberId(), roleMember.isLead())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RoleMember>> response = client.toBlocking().exchange(request, Argument.setOf(RoleMember.class));

        assertEquals(Set.of(roleMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateRoleMemberByAdmin() {
        MemberProfile user = createAnUnrelatedUser();
        createDefaultAdminRole(user);

        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        RoleMemberUpdateDTO roleMemberUpdateDTO = new RoleMemberUpdateDTO(roleMember.getId(), roleMember.getRoleId(), roleMember.getMemberId(), true);
        final MutableHttpRequest<RoleMemberUpdateDTO> request = HttpRequest.PUT("", roleMemberUpdateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        RoleMember result = response.body();
        assertNotNull(result);
        assertEquals(roleMember.getMemberId(), result.getMemberId());
        assertTrue(result.isLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), roleMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateRoleMemberByRoleLead() {
        Role role = createDefaultRole();

        // Create a role lead and add him to the role
        MemberProfile memberProfileOfRoleLead = createADefaultMemberProfile();
        createLeadRoleMember(role, memberProfileOfRoleLead);

        // Create a member and add him to role
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        RoleMember roleMember = createDefaultRoleMember(role, memberProfileOfUser);

        // Update member
        RoleMemberUpdateDTO roleMemberUpdateDTO = new RoleMemberUpdateDTO(roleMember.getId(), roleMember.getRoleId(), roleMember.getMemberId(), true);
        final MutableHttpRequest<RoleMemberUpdateDTO> request = HttpRequest.PUT("", roleMemberUpdateDTO).basicAuth(memberProfileOfRoleLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        RoleMember result = response.body();
        assertNotNull(result);
        assertEquals(roleMember.getMemberId(), result.getMemberId());
        assertTrue(result.isLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), roleMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateRoleMemberThrowsExceptionForNotAdminAndNotRoleLead() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<RoleMember> request = HttpRequest.PUT("", roleMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateAnInvalidRoleMember() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        roleMember.setMemberId(null);
        roleMember.setRoleId(null);

        final HttpRequest<RoleMember> request = HttpRequest.PUT("", roleMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("roleMember.memberId: must not be null", errorList.get(0));
        assertEquals("roleMember.roleId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullRoleMember() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [roleMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateRoleMemberThrowException() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        roleMember.setMemberId(UUID.randomUUID());
        roleMember.setRoleId(roleMember.getRoleId());

        final MutableHttpRequest<RoleMember> request = HttpRequest.PUT("", roleMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", roleMember.getMemberId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateRoleMemberThrowExceptionWithNoRole() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        roleMember.setMemberId(roleMember.getMemberId());
        roleMember.setRoleId(UUID.randomUUID());

        final MutableHttpRequest<RoleMember> request = HttpRequest.PUT("", roleMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Role %s doesn't exist", roleMember.getRoleId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateRoleMemberThrowExceptionWithInvalidId() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        roleMember.setId(UUID.randomUUID());
        roleMember.setMemberId(roleMember.getMemberId());
        roleMember.setRoleId(roleMember.getRoleId());

        final MutableHttpRequest<RoleMember> request = HttpRequest.PUT("", roleMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate roleMember to update with id %s", roleMember.getId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testDeleteRoleMemberAsAdmin() {
        MemberProfile user = createAnUnrelatedUser();
        createDefaultAdminRole(user);

        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", roleMember.getId())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteRoleMemberWithoutAdminPrivilege() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", roleMember.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testDeleteRoleMemberWithRoleLead() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile leadMemberProfile = createAnUnrelatedUser();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        RoleMember roleLead = createLeadRoleMember(role, leadMemberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", roleMember.getId())).basicAuth(leadMemberProfile.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteInvalidRoleMemberAsAdmin() {
        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
        roleMember.setId(UUID.randomUUID());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", roleMember.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

//    @Test
//    void testMemberHistoryTableIsCreatedWhenRoleMemberIsAdded() {
//
//        Role role = createDefaultRole();
//
//        long numHistoryRows = getMemberHistoryRepository().count();
//
//        // Create a role lead and add him to the role
//        MemberProfile memberProfileOfRoleLead = createADefaultMemberProfile();
//        createLeadRoleMember(role, memberProfileOfRoleLead);
//
//        // Create a member and add him to role
//        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
//        RoleMemberCreateDTO roleMemberCreateDTO = new RoleMemberCreateDTO(role.getId(), memberProfileOfUser.getId(), false);
//        final HttpRequest<RoleMemberCreateDTO> request = HttpRequest.POST("", roleMemberCreateDTO).basicAuth(memberProfileOfRoleLead.getWorkEmail(), MEMBER_ROLE);
//        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);
//
//        RoleMember roleMember = response.body();
//
//
//        assertEquals(numHistoryRows + 1, getMemberHistoryRepository().count());
//
//        final List<MemberHistory> actualEntries = getMemberHistoryRepository().findByRoleIdAndMemberId(role.getId(), memberProfileOfUser.getId());
//        actualEntries.sort(Comparator.comparing(MemberHistory::getDate));
//        MemberHistory last = actualEntries.get(actualEntries.size() - 1);
//
//        assertEquals("Added", last.getChange());
//
//        assertEquals(roleMemberCreateDTO.getMemberId(), roleMember.getMemberId());
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), roleMember.getId()), response.getHeaders().get("location"));
//    }

//    @Test
//    void testMemberHistoryTableIsCreatedWhenRoleMemberIsUpdated() {
//        MemberProfile user = createAnUnrelatedUser();
//        createDefaultAdminRole(user);
//
//        Role role = createDefaultRole();
//        MemberProfile memberProfile = createADefaultMemberProfile();
//
//        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);
//
//        long numHistoryRows = getMemberHistoryRepository().count();
//
//        RoleMemberUpdateDTO roleMemberUpdateDTO = new RoleMemberUpdateDTO(roleMember.getId(), roleMember.getRoleId(), roleMember.getMemberId(), true);
//        final MutableHttpRequest<RoleMemberUpdateDTO> request = HttpRequest.PUT("", roleMemberUpdateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
//        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);
//
//        assertEquals(numHistoryRows + 1, getMemberHistoryRepository().count());
//
//        final List<MemberHistory> actualEntries = getMemberHistoryRepository().findByRoleIdAndMemberId(role.getId(), memberProfile.getId());
//        actualEntries.sort(Comparator.comparing(MemberHistory::getDate));
//        MemberHistory last = actualEntries.get(actualEntries.size() - 1);
//
//        assertEquals("Updated", last.getChange());
//
//        RoleMember result = response.body();
//        assertNotNull(result);
//        assertEquals(roleMember.getMemberId(), result.getMemberId());
//        assertTrue(result.isLead());
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), roleMember.getId()), response.getHeaders().get("location"));
//    }

    @Test
    void testMemberHistoryTableIsCreatedWhenRoleMemberIsRemoved() {
        MemberProfile user = createAnUnrelatedUser();
        createDefaultAdminRole(user);

        Role role = createDefaultRole();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RoleMember roleMember = createDefaultRoleMember(role, memberProfile);

        long numHistoryRows = getMemberHistoryRepository().count();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", roleMember.getId())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<RoleMember> response = client.toBlocking().exchange(request, RoleMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(numHistoryRows + 1, getMemberHistoryRepository().count());

//        final List<MemberHistory> actualEntries = getMemberHistoryRepository().findByRoleIdAndMemberId(role.getId(), memberProfile.getId());
//        actualEntries.sort(Comparator.comparing(MemberHistory::getDate));
//        MemberHistory last = actualEntries.get(actualEntries.size() - 1);
//
//        assertEquals("Deleted", last.getChange());
    }

}
