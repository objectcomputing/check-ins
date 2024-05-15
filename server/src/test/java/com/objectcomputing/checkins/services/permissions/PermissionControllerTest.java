package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.PermissionFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionControllerTest extends TestContainersSuite implements PermissionFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/permissions")
    HttpClient client;

    @Test
    void testGetAllPermissionsEnsureAlphabeticalOrder() {
        // create role and permissions
        Role memberRole = createRole(new Role(RoleType.MEMBER.name(), "Member Role"));
        setPermissionsForMember(memberRole.getId());

        // assign role to user
        MemberProfile user = createADefaultMemberProfile();
        assignMemberRole(user);

        List<PermissionDTO> expected = List.of(Permission.values()).stream().map((permission) -> new PermissionDTO(permission)).collect(Collectors.toList());
        final HttpRequest<Object> request = HttpRequest.
                GET("/OrderByPermission").basicAuth(user.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);

        final HttpResponse<List<PermissionDTO>> response =
                client.toBlocking().exchange(request, Argument.listOf(PermissionDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(expected, response.getBody().get());
    }

    @Test
    void getOrderByPermissionIsNotAuthenticatedThrowsError() {
        final HttpRequest<Object> request = HttpRequest.GET("/OrderByPermission");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void testGetAllPermissions() {
        // create role and permissions
        Role memberRole = createRole(new Role(RoleType.MEMBER.name(), "Member Role"));
        setPermissionsForMember(memberRole.getId());

        // assign role to user
        MemberProfile user = createADefaultMemberProfile();
        assignMemberRole(user);

        List<PermissionDTO> expected = List.of(Permission.values()).stream().map((permission) -> new PermissionDTO(permission)).collect(Collectors.toList());
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(user.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);

        final HttpResponse<List<PermissionDTO>> response =
                client.toBlocking().exchange(request, Argument.listOf(PermissionDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(expected, response.getBody().get());
    }

    @Test
    void getAllPermissionsIsNotAuthenticatedThrowsError() {
        final HttpRequest<Object> request = HttpRequest.GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

}
