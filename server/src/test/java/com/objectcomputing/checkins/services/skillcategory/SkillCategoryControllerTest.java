package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillCategoryFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class SkillCategoryControllerTest extends TestContainersSuite implements SkillCategoryFixture {

    @Inject
    @Client("/services/skills/categories")
    private HttpClient client;

    @Test
    public void testPost() {
        SkillCategoryCreateDTO createDTO = new SkillCategoryCreateDTO();
        createDTO.setName("Languages");
        createDTO.setDescription("Programming Languages");

        final HttpRequest<SkillCategoryCreateDTO> request = HttpRequest
                .POST("/", createDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<SkillCategory> response = client.toBlocking().exchange(request, SkillCategory.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(createDTO.getName(), response.getBody().get().getName());
        assertEquals(createDTO.getDescription(), response.getBody().get().getDescription());

        String expectedURI = String.format("%s/%s", request.getPath(), response.getBody().get().getId());
        assertEquals(expectedURI, response.getHeaders().get("location"));
    }

    @Test
    public void testCreateSkillCategoryAlreadyExists() {
        SkillCategory existingCategory = createDefaultSkillCategory();

        SkillCategoryCreateDTO createDTO = new SkillCategoryCreateDTO();
        createDTO.setName(existingCategory.getName());

        final HttpRequest<SkillCategoryCreateDTO> request = HttpRequest
                .POST("/", createDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
    }

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

    @Test
    public void testGetByIdNotFound() {
        final HttpRequest<?> request = HttpRequest
                .GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, SkillCategory.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    public void testFindAll() {
        SkillCategory skillCategory = createDefaultSkillCategory();

        final HttpRequest<?> request = HttpRequest
                .GET("/")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<SkillCategory>> response = client
                .toBlocking()
                .exchange(request, Argument.listOf(SkillCategory.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Collections.singletonList(skillCategory), response.getBody().orElseThrow());
    }

}