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
}
