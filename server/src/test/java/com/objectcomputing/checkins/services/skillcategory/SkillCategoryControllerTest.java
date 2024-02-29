package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class SkillCategoryControllerTest extends TestContainersSuite {

    @Inject
    @Client("/services/skills/categories")
    private HttpClient client;

    @Test
    public void testGetByIdHappyPath() {
        UUID id = UUID.randomUUID();
        SkillCategory skillCategory = new SkillCategory(id, "Languages", "Programming Languages");

        final HttpRequest<Object> request = HttpRequest
                .GET(String.format("/%s", id))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<SkillCategory> response = client.toBlocking().exchange(request, SkillCategory.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        SkillCategory body = Objects.requireNonNull(response.body());
        assertEquals(skillCategory.getName(), body.getName());
        assertEquals(skillCategory.getDescription(), body.getDescription());
    }

}