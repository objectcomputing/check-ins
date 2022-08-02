package com.objectcomputing.checkins.services.education;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EducationFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.education.EducationTestUtil.*;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;

public class EducationControllerTest extends TestContainersSuite implements EducationFixture, RoleFixture {

    private static final Logger LOG = LoggerFactory.getLogger(EducationController.class);

    @Inject
    @Client("/services/education")
    private HttpClient client;

    @Test
    public void testGETGetById() {

        Education education = createEducation();

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", education.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<EducationDTO> response = client.toBlocking().exchange(request,
                EducationDTO.class);

        assertEducationEqual(education, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
