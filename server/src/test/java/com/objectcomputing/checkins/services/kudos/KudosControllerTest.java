package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KudosControllerTest extends TestContainersSuite implements KudosFixture {

    @Inject
    @Client("/services/kudos")
    HttpClient client;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateKudos() {

    }

    @Test
    void testApproveKudos() {
    }

    @Test
    void testGetKudosById() {
    }

    @Test
    void testGetKudos() {
    }

    @Test
    void testDeleteKudos() {
        Kudos kudos = createADefaultKudos();
        final HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s", kudos.getId()))
                .basicAuth("", RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<Kudos> response = client.toBlocking().exchange(request, Kudos.class);

        // assertEquals(HttpStatus.OK, response.getStatus());
    }
}