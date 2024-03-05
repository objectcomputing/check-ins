package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillCategoryFixture;
import com.objectcomputing.checkins.services.fixture.SkillCategorySkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.skills.Skill;
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

public class SkillCategoryControllerTest extends TestContainersSuite
        implements SkillCategoryFixture, SkillFixture, SkillCategorySkillFixture {

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
    public void testUpdateSkillCategory() {
        SkillCategory existingCategory = createDefaultSkillCategory();

        SkillCategoryUpdateDTO updateDTO = new SkillCategoryUpdateDTO();
        updateDTO.setId(existingCategory.getId());
        updateDTO.setName(existingCategory.getName());
        String expectedDescription = "FOOBAR";
        updateDTO.setDescription(expectedDescription);
        final HttpRequest<SkillCategoryUpdateDTO> request = HttpRequest
                .PUT("/", updateDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<SkillCategory> response = client.toBlocking().exchange(request, SkillCategory.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testUpdateSkillCategoryIdDoesNotExist() {
        SkillCategory existingCategory = createDefaultSkillCategory();
        SkillCategoryUpdateDTO updateDTO = new SkillCategoryUpdateDTO();
        updateDTO.setId(UUID.randomUUID());
        updateDTO.setName(existingCategory.getName());
        String expectedDescription = "FOOBAR";
        updateDTO.setDescription(expectedDescription);
        final HttpRequest<SkillCategoryUpdateDTO> request = HttpRequest
                .PUT("/", updateDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testUpdateSkillCategory_nameAlreadyExists() {
        SkillCategory existingCategory1 = createDefaultSkillCategory();
        SkillCategory another = createAnotherSkillCategory();

        SkillCategoryUpdateDTO updateDTO = new SkillCategoryUpdateDTO();
        updateDTO.setId(existingCategory1.getId());
        updateDTO.setName(another.getName());
        final HttpRequest<SkillCategoryUpdateDTO> request = HttpRequest
                .PUT("/", updateDTO)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
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

    @Test
    public void testFindAllWithSkills() {
        SkillCategory skillCategory = createDefaultSkillCategory();
        Skill skill = createADefaultSkill();
        createSkillCategorySkill(skillCategory.getId(), skill.getId());

        List<SkillCategoryResponseDTO> expectedList = new ArrayList<>();
        SkillCategoryResponseDTO dto = new SkillCategoryResponseDTO();
        dto.setId(skillCategory.getId());
        dto.setName(skillCategory.getName());
        dto.setDescription(skillCategory.getDescription());
        dto.setSkills(Collections.singletonList(skill));
        expectedList.add(dto);

        final HttpRequest<?> request = HttpRequest
                .GET("/with-skills")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<SkillCategoryResponseDTO>> response = client
                .toBlocking()
                .exchange(request, Argument.listOf(SkillCategoryResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedList, response.getBody().orElseThrow());
    }

    @Test
    public void testFindAllWithoutSkills() {
        SkillCategory skillCategory = createDefaultSkillCategory();

        List<SkillCategoryResponseDTO> expectedList = new ArrayList<>();
        SkillCategoryResponseDTO dto = new SkillCategoryResponseDTO();
        dto.setId(skillCategory.getId());
        dto.setName(skillCategory.getName());
        dto.setDescription(skillCategory.getDescription());
        dto.setSkills(Collections.emptyList());
        expectedList.add(dto);

        final HttpRequest<?> request = HttpRequest
                .GET("/with-skills")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<SkillCategoryResponseDTO>> response = client
                .toBlocking()
                .exchange(request, Argument.listOf(SkillCategoryResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedList, response.getBody().orElseThrow());
    }

}