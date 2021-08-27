package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.PermissionFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PermissionsControllerTest extends TestContainersSuite implements PermissionFixture {

    @Inject
    @Client("/services/permissions")
    HttpClient client;

    @Test
    void testGetAllPermissions() {
        Permission permission = createADefaultPermission();
        Permission otherPermission = createADifferentPermission();
        List<Permission> list = new ArrayList<>(Arrays.asList(permission, otherPermission));
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<List<Permission>> response =
                client.toBlocking().exchange(request, Argument.listOf(Permission.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(response.getBody().get(), list);
    }

    @Test
    void testGetAllPermissionsEnsureAlphabeticalOrder() {
        Permission lastPermission = createACustomPermission("z");
        Permission firstPermission = createACustomPermission("A");
        Permission middlePermisison = createACustomPermission("H");
        List<Permission> list = new ArrayList<>(Arrays.asList(firstPermission, middlePermisison, lastPermission));
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<List<Permission>> response =
                client.toBlocking().exchange(request, Argument.listOf(Permission.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(response.getBody().get(), list);
    }

    @Test
    void testGetPermissionsNoneExistsReturnsEmptyList(){
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<List<Permission>> response =
                client.toBlocking().exchange(request, Argument.listOf(Permission.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertTrue(response.getBody().isPresent());
        assertEquals(response.getBody().get(), List.of());
    }

    @Test
    void getPermissionsIsNotAuthenticatedThrowsError(){
        final HttpRequest<Object> request = HttpRequest.
                GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

}
