package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class SkillCategoryControllerTest extends TestContainersSuite
        implements SkillCategoryFixture, SkillFixture, SkillCategorySkillFixture, RoleFixture {

    @Inject
    @Client("/services/skills/categories")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testPost() {
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
    void testCreateSkillCategoryAlreadyExists() {
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
    void testCreateNotAllowed() {
        SkillCategoryCreateDTO createDTO = new SkillCategoryCreateDTO();

        final HttpRequest<SkillCategoryCreateDTO> request = HttpRequest
                .POST("/", createDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testUpdateSkillCategory() {
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
    void testUpdateNotAllowed() {
        SkillCategoryUpdateDTO updateDTO = new SkillCategoryUpdateDTO();

        final HttpRequest<SkillCategoryUpdateDTO> request = HttpRequest
                .PUT("/", updateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testUpdateSkillCategoryIdDoesNotExist() {
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
    void testUpdateSkillCategory_nameAlreadyExists() {
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
    void testGetByIdHappyPath() {
        SkillCategory skillCategory = createDefaultSkillCategory();
        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();
        createSkillCategorySkill(skillCategory.getId(), skill.getId());
        createSkillCategorySkill(skillCategory.getId(), skill2.getId());
        SkillCategoryResponseDTO expectedDto = SkillCategoryResponseDTO.create(skillCategory, List.of(skill, skill2));

        final HttpRequest<Object> request = HttpRequest
                .GET(String.format("/%s", skillCategory.getId()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<SkillCategoryResponseDTO> response = client.toBlocking().exchange(request, SkillCategoryResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        SkillCategoryResponseDTO body = Objects.requireNonNull(response.body());
        assertEquals(expectedDto.getSkills().size(), body.getSkills().size());
        assertEquals(expectedDto, body);
    }

    @Test
    void testGetByIdNotAllowed() {
        final HttpRequest<Object> request = HttpRequest
                .GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, SkillCategoryResponseDTO.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testGetByIdNotFound() {
        final HttpRequest<?> request = HttpRequest
                .GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, SkillCategoryResponseDTO.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllWithSkills() {
        SkillCategory skillCategory = createDefaultSkillCategory();
        Skill skill = createADefaultSkill();
        createSkillCategorySkill(skillCategory.getId(), skill.getId());

        List<SkillCategoryResponseDTO> expectedList = new ArrayList<>();
        SkillCategoryResponseDTO dto = SkillCategoryResponseDTO.create(skillCategory, Collections.singletonList(skill));
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
    void testFindAllWithSkillsAlphabetical() {
        // If properly sorted, "Languages" should come before "Libraries" despite having a different order in the database
        SkillCategory librariesCategory = createAnotherSkillCategory();
        SkillCategory languagesCategory = createDefaultSkillCategory();

        List<SkillCategoryResponseDTO> expectedList = new ArrayList<>();
        SkillCategoryResponseDTO dto = SkillCategoryResponseDTO.create(languagesCategory, Collections.emptyList());
        expectedList.add(dto);
        SkillCategoryResponseDTO dto2 = SkillCategoryResponseDTO.create(librariesCategory, Collections.emptyList());
        expectedList.add(dto2);

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
    void testFindAllNotAllowed() {
        final HttpRequest<Object> request = HttpRequest
                .GET("/with-skills")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.listOf(SkillCategoryResponseDTO.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testFindAllWithoutSkills() {
        SkillCategory skillCategory = createDefaultSkillCategory();

        List<SkillCategoryResponseDTO> expectedList = new ArrayList<>();
        SkillCategoryResponseDTO dto = SkillCategoryResponseDTO.create(skillCategory, Collections.emptyList());
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
    void testDeleteAllInCategory() {
        SkillCategory category = createDefaultSkillCategory();
        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();
        createSkillCategorySkill(category.getId(), skill.getId());
        createSkillCategorySkill(category.getId(), skill2.getId());

        final HttpRequest<?> request = HttpRequest
                .DELETE(String.format("/%s", category.getId()))
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(getSkillCategorySkillRepository().findAllBySkillCategoryId(category.getId()).isEmpty());
    }

    @Test
    void testDeleteNotAllowed() {
        final HttpRequest<?> request = HttpRequest
                .DELETE(String.format("/%s", UUID.randomUUID()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.listOf(SkillCategoryResponseDTO.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}