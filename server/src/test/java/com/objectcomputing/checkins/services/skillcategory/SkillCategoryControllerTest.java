package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillCategoryFixture;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class SkillCategoryControllerTest extends TestContainersSuite implements SkillCategoryFixture {

    @Inject
    @Client("/services/skills/categories")
    private HttpClient client;

    @Test
    public void testGetByIdHappyPath() {
        SkillCategory skillCategory = createDefaultSkillCategory();

        final HttpRequest<Object> request = HttpRequest
                .GET(String.format("/%s", skillCategory.getId()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<SkillCategory> response = client.toBlocking().exchange(request, SkillCategory.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        SkillCategory body = Objects.requireNonNull(response.body());
        assertEquals(skillCategory, body);
    }

}