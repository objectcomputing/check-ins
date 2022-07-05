package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
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
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PermissionsControllerTest extends TestContainersSuite implements PermissionFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/permissions")
    HttpClient client;

    @Test
    void testGetAllPermissionsEnsureAlphabeticalOrder() {
        // create role and permissions
        Role memberRole = createRole(new Role(RoleType.MEMBER.name(), "Member Role"));
        Permission someTestPermission = createACustomPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS);
        Permission someOtherPermission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);
        setPermissionsForMember(memberRole.getId());

        // assign role to user
        MemberProfile user = createADefaultMemberProfile();
        assignMemberRole(user);

        List<Permission> expected = new ArrayList<>(Arrays.asList(someOtherPermission, someTestPermission));
        final HttpRequest<Object> request = HttpRequest.
                GET("/OrderByPermission").basicAuth(user.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);

        final HttpResponse<List<Permission>> response =
                client.toBlocking().exchange(request, Argument.listOf(Permission.class));

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
        Permission someTestPermission = createACustomPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS);
        Permission someOtherPermission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);
        setPermissionsForMember(memberRole.getId());

        // assign role to user
        MemberProfile user = createADefaultMemberProfile();
        assignMemberRole(user);

        List<Permission> expected = new ArrayList<>(Arrays.asList(someTestPermission, someOtherPermission));
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(user.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);

        final HttpResponse<List<Permission>> response =
                client.toBlocking().exchange(request, Argument.listOf(Permission.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(expected, response.getBody().get());
    }

    @Test
    void getAllPermissionsnIsNotAuthenticatedThrowsError() {
        final HttpRequest<Object> request = HttpRequest.GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void testGetUserPermissions() {
        MemberProfile currentUser = createADefaultMemberProfile();
        MemberProfile adminUser = createASecondDefaultMemberProfile();
        Role memberRole = createAndAssignRole(RoleType.MEMBER, currentUser);
        Role adminRole = createAndAssignAdminRole(adminUser);

        Permission memberPermission1 = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);
        Permission memberPermission2 = createACustomPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS);
        Permission adminPermission = createACustomPermission(Permissions.CAN_CREATE_ORGANIZATION_MEMBERS);

        setRolePermission(memberRole.getId(), memberPermission1.getId());
        setRolePermission(memberRole.getId(), memberPermission2.getId());
        setRolePermission(adminRole.getId(), adminPermission.getId());

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", currentUser.getId()))
                .basicAuth(currentUser.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<Permission>> response = client.toBlocking()
                .exchange(request, Argument.listOf(Permission.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertTrue(response.getBody().get().contains(memberPermission1));
        assertTrue(response.getBody().get().contains(memberPermission2));
    }

    @Test
    void testGetUserPermissionsNotAuthorized() {
        MemberProfile currentUser = createADefaultMemberProfile();
        MemberProfile otherUser = createAnUnrelatedUser();
        Role memberRole = createAndAssignRole(RoleType.MEMBER, currentUser);

        Permission permission1 = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);
        Permission permission2 = createACustomPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS);

        setRolePermission(memberRole.getId(), permission1.getId());
        setRolePermission(memberRole.getId(), permission2.getId());

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", otherUser.getId()))
                .basicAuth(currentUser.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("You are not allowed to do this operation", responseException.getMessage());
    }

}
