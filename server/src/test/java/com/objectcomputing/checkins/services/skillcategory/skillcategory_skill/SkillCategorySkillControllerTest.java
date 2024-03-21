package com.objectcomputing.checkins.services.skillcategory.skillcategory_skill;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SkillCategoryFixture;
import com.objectcomputing.checkins.services.fixture.SkillCategorySkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.skillcategory.SkillCategory;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

class SkillCategorySkillControllerTest extends TestContainersSuite
        implements SkillCategoryFixture, SkillFixture, SkillCategorySkillFixture, RoleFixture {

    @Inject
    @Client("/services/skills/category-skills")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    public void testCreate() {
        SkillCategory defaultSkillCategory = createDefaultSkillCategory();
        Skill aDefaultSkill = createADefaultSkill();
        SkillCategorySkill skillCategorySkill = new SkillCategorySkill(defaultSkillCategory.getId(),aDefaultSkill.getId());
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .POST("/", skillCategorySkill.getSkillCategorySkillId())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<SkillCategorySkill> response = client.toBlocking().exchange(httpRequest, SkillCategorySkill.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        SkillCategorySkill expected = getSkillCategorySkillRepository().findAll().get(0);
        assertEquals(expected, response.getBody().get());
    }

    @Test
    public void testCreateFail() {
        Skill aDefaultSkill = createADefaultSkill();
        createDefaultSkillCategory();
        UUID id = UUID.randomUUID();
        SkillCategorySkill skillCategorySkill = new SkillCategorySkill(id, aDefaultSkill.getId());
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .POST("/", skillCategorySkill.getSkillCategorySkillId())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testCreateNullIds() {
        createADefaultSkill();
        createDefaultSkillCategory();
        SkillCategorySkill skillCategorySkill = new SkillCategorySkill(null, null);
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .POST("/", skillCategorySkill.getSkillCategorySkillId())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testCreateNotAllowed() {
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .POST("/", new SkillCategorySkillId())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    public void testDelete() {
        SkillCategory defaultSkillCategory = createDefaultSkillCategory();
        Skill aDefaultSkill = createADefaultSkill();
        SkillCategorySkill skillCategorySkill = createSkillCategorySkill(defaultSkillCategory.getId(), aDefaultSkill.getId());
        SkillCategorySkillId skillCategorySkillId =  skillCategorySkill.getSkillCategorySkillId();
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .DELETE("/", skillCategorySkillId)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<SkillCategorySkill> response = client.toBlocking().exchange(httpRequest, SkillCategorySkill.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.getBody().isPresent());
        List<SkillCategorySkill> all = getSkillCategorySkillRepository().findAll();
        assertEquals(0, all.size());
    }


    @Test
    public void testDeleteDontExist() {
        SkillCategorySkillId skillCategorySkillId =  new SkillCategorySkillId(UUID.randomUUID(),UUID.randomUUID());
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .DELETE("/", skillCategorySkillId)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<SkillCategorySkill> response = client.toBlocking().exchange(httpRequest, SkillCategorySkill.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.getBody().isPresent());
    }

    @Test
    public void testDeleteNotAllowed() {
        HttpRequest<SkillCategorySkillId> httpRequest = HttpRequest
                .DELETE("/", new SkillCategorySkillId())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(httpRequest, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}