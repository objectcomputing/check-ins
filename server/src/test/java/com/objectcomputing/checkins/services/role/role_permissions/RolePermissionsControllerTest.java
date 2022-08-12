package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.permissions.Permission;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RolePermissionsControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/roles/role-permissions")
    HttpClient client;

    @Test
    void testGetAllRolePermissions() {
        // create role and permissions
        Role adminRole = createRole(new Role(RoleType.ADMIN.name(), "Admin Role"));
        Permission someTestPermission = createACustomPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS);
        Permission someOtherPermission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);
        setPermissionsForAdmin(adminRole.getId());

        // assign role to user
        MemberProfile user = createADefaultMemberProfile();
        assignAdminRole(user);

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(user.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);

        final HttpResponse<List<RolePermissionResponseDTO>> response =
                client.toBlocking().exchange(request, Argument.listOf(RolePermissionResponseDTO.class));


        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());

        List<RolePermissionResponseDTO> actual = response.getBody().get();
        assertEquals(adminRole.getId(), actual.get(0).getRoleId());
        assertEquals(adminRole.getRole(), actual.get(0).getRole());
        assertNotNull(actual.get(0).getDescription());
        assertEquals(2, actual.get(0).getPermissions().size());
        assertTrue(actual.get(0).getPermissions().contains(someTestPermission));
        assertTrue(actual.get(0).getPermissions().contains(someOtherPermission));
    }

    @Test
    void getAllRolePermissionsThrowsError() {
        final HttpRequest<Object> request = HttpRequest.GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void testCreateRolePermission() {
        MemberProfile admin = createAnUnrelatedUser();
        Role adminRole = createAndAssignAdminRole(admin);
        Permission permission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);

        RolePermissionId rolePermissionId = new RolePermissionId(adminRole.getId(), permission.getId());

        final HttpRequest<RolePermissionId> request = HttpRequest.POST("", rolePermissionId)
                .basicAuth(admin.getWorkEmail(), adminRole.getRole());
        final HttpResponse<RolePermission> response = client.toBlocking().exchange(request, RolePermission.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(adminRole.getId(), response.getBody().get().getRolePermissionId().getRoleId());
        assertEquals(permission.getId(), response.getBody().get().getRolePermissionId().getPermissionId());
    }

    @Test
    void testCreateRolePermissionNotAuthorized() {
        MemberProfile user = createADefaultMemberProfile();
        Role adminRole = createRole(new Role(RoleType.ADMIN.name(), "Admin Role"));
        Permission permission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);

        RolePermission rolePermission = new RolePermission(adminRole.getId(), permission.getId());

        final HttpRequest<RolePermission> request = HttpRequest.POST("", rolePermission)
                .basicAuth(user.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateWhereRoleDoesNotExist() {
        MemberProfile admin = createAnUnrelatedUser();
        UUID nonexistentRoleId = UUID.randomUUID();
        Permission permission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);

        RolePermissionId rolePermissionId = new RolePermissionId(nonexistentRoleId, permission.getId());

        final HttpRequest<RolePermissionId> request = HttpRequest.POST("", rolePermissionId)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Attempted to save role permission where role %s does not exist", nonexistentRoleId), responseException.getMessage());
    }

    @Test
    void testCreateWherePermissionDoesNotExist() {
        MemberProfile admin = createAnUnrelatedUser();
        Role adminRole = createAndAssignAdminRole(admin);
        UUID nonexistentPermissionId = UUID.randomUUID();

        RolePermissionId rolePermissionId = new RolePermissionId(adminRole.getId(), nonexistentPermissionId);

        final HttpRequest<RolePermissionId> request = HttpRequest.POST("", rolePermissionId)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Attempted to save role permission where permission %s does not exist", nonexistentPermissionId), responseException.getMessage());
    }

    @Test
    void testCreateDuplicateRolePermissionNotAllowed() {
        MemberProfile admin = createAnUnrelatedUser();
        Role adminRole = createAndAssignAdminRole(admin);
        Permission permission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);

        // Grant admin role the permission
        setRolePermission(adminRole.getId(), permission.getId());

        // Attempt to save the same permission for the admin role
        RolePermissionId rolePermissionId = new RolePermissionId(adminRole.getId(), permission.getId());

        final HttpRequest<RolePermissionId> request = HttpRequest.POST("", rolePermissionId)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Attempted to save role permission where role %s already has permission %s", adminRole.getId(), permission.getId()), responseException.getMessage());
    }

    @Test
    void testDeleteRolePermission() {
        MemberProfile admin = createAnUnrelatedUser();
        Role adminRole = createAndAssignAdminRole(admin);
        Permission permission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);

        setRolePermission(adminRole.getId(), permission.getId());

        final HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s/%s", adminRole.getId(), permission.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteRolePermissionNotAuthorized() {
        MemberProfile user = createADefaultMemberProfile();
        Role adminRole = createRole(new Role(RoleType.ADMIN.name(), "Admin Role"));
        Permission permission = createACustomPermission(Permissions.CAN_VIEW_PERMISSIONS);

        setRolePermission(adminRole.getId(), permission.getId());

        final HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s/%s", adminRole.getId(), permission.getId()))
                .basicAuth(user.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }
}
