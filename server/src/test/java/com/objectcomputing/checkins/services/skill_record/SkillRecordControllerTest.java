package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

class SkillRecordControllerTest extends TestContainersSuite implements RoleFixture, SkillFixture {

    @Inject
    @Client("/services/skills/records")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testGetSuccess() {
        HttpRequest<?> request = HttpRequest
                .GET("/csv")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
    }

    @Test
    void testGetNotAllowed() {
        HttpRequest<?> request = HttpRequest
                .GET("/csv")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, File.class)
        );

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

}