package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

class SkillControllerTest extends TestContainersSuite implements SkillFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/skills")
    private HttpClient client;

    private MemberProfile member;
    private MemberProfile admin;

    @BeforeEach
    void makeRoles() {
        createAndAssignRoles();
        member = createADefaultMemberProfile();
        admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Test
    void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
                    .basicAuth(member.getWorkEmail(), MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testGETFindByNameReturnsEmptyBody() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testGETFindByValueName() {

        Skill skill = createADefaultSkill();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue(skill.getName()))).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(Set.of(skill), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testGETFindByValuePending() {

        Skill skill = createADefaultSkill();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?pending=%s", encodeValue(String.valueOf(skill.isPending())))).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(Set.of(skill), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testGETGetByIdHappyPath() {

        Skill skill = createADefaultSkill();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", skill.getId())).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        assertEquals(skill, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

    }

    @Test
    void testPOSTCreateASkill() {

        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
        skillCreateDTO.setName("reincarnation");
        skillCreateDTO.setPending(true);
        skillCreateDTO.setExtraneous(true);
        skillCreateDTO.setDescription("Bring back from the dead");

        final HttpRequest<SkillCreateDTO> request = HttpRequest.
                POST("/", skillCreateDTO).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(skillCreateDTO.getName(), response.body().getName());
        assertEquals(skillCreateDTO.isExtraneous(), response.body().isExtraneous());
        assertEquals(skillCreateDTO.getDescription(), response.body().getDescription());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), response.getHeaders().get("location"));
    }

    @Test
    void testPOSTCreateASkillAlreadyExists() {

        Skill skill = createADefaultSkill();
        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
        skillCreateDTO.setName(skill.getName());
        skillCreateDTO.setPending(skill.isPending());

        final HttpRequest<SkillCreateDTO> request = HttpRequest.
                POST("/", skillCreateDTO).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());

    }

    @Test
    void testPOSTCreateASkillAlreadyExistsWhenPending() {

        Skill skill = createADefaultSkill();
        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
        skillCreateDTO.setName(skill.getName());
        skillCreateDTO.setPending(false);

        final HttpRequest<SkillCreateDTO> request = HttpRequest.
                POST("/", skillCreateDTO).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());

    }

    @Test
    void testPOSTCreateANullSkill() {

        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();

        final HttpRequest<SkillCreateDTO> request = HttpRequest.
                POST("/", skillCreateDTO).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateSkillAsAdmin() {

        Skill skill = createADefaultSkill();

        final HttpRequest<Skill> request = HttpRequest.
                PUT("/", skill).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        assertEquals(skill, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testPUTUpdateSkillNonAdmin() {

        Skill skill = createADefaultSkill();

        final HttpRequest<Skill> request = HttpRequest.PUT("/", skill)
                .basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testPUTUpdateNonexistentSkill() {

        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
        skillCreateDTO.setName("reincarnation");
        skillCreateDTO.setPending(true);

        final HttpRequest<SkillCreateDTO> request = HttpRequest.
                PUT("/", skillCreateDTO).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testPUTUpdateNullSkill() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void deleteSkillAsAdmin() {

        Skill skill = createADefaultSkill();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", skill.getId())).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void deleteSkillNotAsAdmin() {

        Skill skill = createADefaultSkill();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", skill.getId())).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}
