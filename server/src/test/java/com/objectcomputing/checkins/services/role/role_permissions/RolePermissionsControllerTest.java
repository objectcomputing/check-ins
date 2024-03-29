package com.objectcomputing.checkins.services.role.role_permissions;

;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.PermissionFixture;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RolePermissionsControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, PermissionFixture {

    @Inject
    @Client("/services/roles/role-permissions")
    HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testGetAllRolePermissions() {
        // assign role to user
        MemberProfile user = createADefaultMemberProfile();
        assignAdminRole(user);

        Role adminRole = getRoleRepository().findByRole(RoleType.ADMIN.name()).orElseThrow();

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(user.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);

        final HttpResponse<List<RolePermissionsResponseDTO>> response =
                client.toBlocking().exchange(request, Argument.listOf(RolePermissionsResponseDTO.class));


        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());

        List<RolePermissionsResponseDTO> actual = response.getBody().get();
        assertEquals(adminRole.getId(), actual.get(0).getRoleId());
        assertEquals(adminRole.getRole(), actual.get(0).getRole());
        assertNotNull(actual.get(0).getDescription());
        assertEquals(17, actual.get(0).getPermissions().size());
        List<Permission> assigned = actual.get(0).getPermissions();
        for(Permission permission: adminPermissions) {
            Permission stored = permission;
            assertTrue(assigned.contains(stored));
        }
    }

    @Test
    void getAllRolePermissionsThrowsError() {
        final HttpRequest<Object> request = HttpRequest.GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void testAssignPermissionToRole() {
        MemberProfile sender = createADefaultMemberProfile();
        assignAdminRole(sender);

        Role memberRole = getRoleRepository().findByRole(RoleType.MEMBER.name()).get();
        Permission birthdayPermission = Permission.CAN_VIEW_BIRTHDAY_REPORT;

        RolePermissionDTO dto = new RolePermissionDTO(memberRole.getId(), birthdayPermission);

        final HttpRequest<?> request = HttpRequest.POST("/", dto)
                .basicAuth(sender.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(1, getRolePermissionRepository().findByIds(memberRole.getId().toString(), birthdayPermission).size());
    }

    @Test
    void testRemovePermissionFromRole() {
        MemberProfile sender = createADefaultMemberProfile();
        assignAdminRole(sender);

        Role memberRole = getRoleRepository().findByRole(RoleType.MEMBER.name()).get();
        Permission birthdayPermission = Permission.CAN_VIEW_BIRTHDAY_REPORT;
        setRolePermission(memberRole.getId(), birthdayPermission);

        final HttpRequest<?> request = HttpRequest.DELETE("/", new RolePermissionDTO(memberRole.getId(), birthdayPermission))
                .basicAuth(sender.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0, getRolePermissionRepository().findByIds(memberRole.getId().toString(), birthdayPermission).size());
    }
}
