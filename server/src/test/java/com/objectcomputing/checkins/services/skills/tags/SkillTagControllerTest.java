package com.objectcomputing.checkins.services.skills.tags;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillResponseDTO;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillTagControllerTest extends TestContainersSuite implements SkillFixture {

    @Inject
    @Client("/services/skillTags")
    private HttpClient client;

    @Test
    public void testGetAllByBoth() {

        Skill skill = createADefaultTaggedSkill();
        final HttpRequest<?> request = HttpRequest.
                GET("/?skillId=" + skill.getId() + "&name=" + skill.getTags().get(0).getName()).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<List<SkillTagResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SkillTagResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(1, response.getBody(Argument.listOf(SkillTagResponseDTO.class)).get().size());

    }

    @Test
    public void testGetAllBySkill() {

        Skill skill = createADefaultTaggedSkill();
        final HttpRequest<?> request = HttpRequest.
                GET("/?skillId=" + skill.getId()).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<List<SkillTagResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SkillTagResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(1, response.getBody(Argument.listOf(SkillTagResponseDTO.class)).get().size());

    }

    @Test
    public void testGetAllByName() {
        SkillTag tag = createADefaultSkillTag();
        final HttpRequest<?> request = HttpRequest.
                GET("/?name=" + tag.getName()).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<List<SkillTagResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SkillTagResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(1, response.getBody(Argument.listOf(SkillTagResponseDTO.class)).get().size());

    }

    @Test
    public void testGetAll() {
        final HttpRequest<?> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<List<SkillTagResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SkillTagResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());

    }

    @Test
    public void testPOSTinvalidBody() {
        SkillTag tag = new SkillTag("", "Just testing");

        final HttpRequest<SkillTagCreateDTO> request = HttpRequest.
                POST("/", createFromEntity(tag)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("dto.name: must not be blank", responseException.getMessage());

    }

    @Test
    public void testPOSTemptyBody() {

        final HttpRequest<String> request = HttpRequest.
                POST("/", "").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Required argument [SkillTagCreateDTO dto] not specified", responseException.getMessage());
    }

    @Test
    public void testPOSThappyPathWithSkill() {
        SkillTag tag = new SkillTag("newName", "Just testing");
        Skill tagMe = createADefaultSkill();
        tag.setSkills(new ArrayList<>());
        tag.getSkills().add(tagMe);

        final HttpRequest<SkillTagCreateDTO> request = HttpRequest.
                POST("/", createFromEntity(tag)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<SkillTagResponseDTO> response = client.toBlocking().exchange(request, SkillTagResponseDTO.class);

        assertEntityDtoEqual(tag, response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testPOSThappyPath() {
        SkillTag tag = createADefaultSkillTag();
        tag.setName("newName");
        tag.setDescription("Just testing");

        final HttpRequest<SkillTagCreateDTO> request = HttpRequest.
                POST("/", createFromEntity(tag)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<SkillTagResponseDTO> response = client.toBlocking().exchange(request, SkillTagResponseDTO.class);

        assertEntityDtoEqual(tag, response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testPUTNotFound() {
        SkillTag tag = createADefaultSkillTag();
        tag.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        tag.setName("newName");
        tag.setDescription("Just testing");

        final HttpRequest<SkillTagUpdateDTO> request = HttpRequest.
                PUT("/", updateFromEntity(tag)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("Tag with id 11111111-1111-1111-1111-111111111111 does not exist", responseException.getMessage());

    }

    @Test
    public void testPUTinvalidBody() {
        SkillTag tag = createADefaultSkillTag();
        tag.setName("");
        tag.setDescription("Just testing");

        final HttpRequest<SkillTagUpdateDTO> request = HttpRequest.
                PUT("/", updateFromEntity(tag)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("dto.name: must not be blank", responseException.getMessage());

    }

    @Test
    public void testPUTemptyBody() {
        SkillTag tag = createADefaultSkillTag();
        tag.setName("newName");
        tag.setDescription("Just testing");

        final HttpRequest<String> request = HttpRequest.
                PUT("/", "").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Required argument [SkillTagUpdateDTO dto] not specified", responseException.getMessage());
    }

    @Test
    public void testPUThappyPath() {
        SkillTag tag = createADefaultSkillTag();
        tag.setName("newName");
        tag.setDescription("Just testing");

        final HttpRequest<SkillTagUpdateDTO> request = HttpRequest.
                PUT("/", updateFromEntity(tag)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<SkillTagResponseDTO> response = client.toBlocking().exchange(request, SkillTagResponseDTO.class);

        assertEntityDtoEqual(tag, response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }


    @Test
    public void testGETGetByIdWithSkill() {

        Skill skill = createADefaultTaggedSkill();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", skill.getTags().get(0).getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<SkillTagResponseDTO> response = client.toBlocking().exchange(request, SkillTagResponseDTO.class);
        SkillTag expected = new SkillTag(skill.getTags().get(0).getName(), skill.getTags().get(0).getDescription());
        expected.setId(skill.getTags().get(0).getId());
        skill.setTags(null);
        expected.setSkills(new ArrayList<>());
        expected.getSkills().add(skill);
        assertEntityDtoEqual(expected, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }


    @Test
    public void testGETGetByIdHappyPath() {

        SkillTag tag = createADefaultSkillTag();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", tag.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<SkillTagResponseDTO> response = client.toBlocking().exchange(request, SkillTagResponseDTO.class);

        assertEntityDtoEqual(tag, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    public void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    public boolean assertEntityDtoEqual(SkillTag entity, SkillTagResponseDTO dto) {
        return Objects.equals(dto.getId(), entity.getId()) &&
                Objects.equals(dto.getName(), entity.getName()) &&
                Objects.equals(dto.getDescription(), entity.getDescription()) &&
                assertSkillEntititiesDtosEqual(entity.getSkills(), dto.getSkills());
    }

    private boolean assertSkillEntititiesDtosEqual(List<Skill> entity, List<SkillResponseDTO> dtoSkills) {
        if (entity == null && dtoSkills == null) {
            return true;
        }
        if ((entity == null && dtoSkills != null) || (entity != null && dtoSkills == null) || entity.size() != dtoSkills.size()) {
            return false;
        }
        for (Skill sk : entity) {
            SkillResponseDTO dto = dtoSkills.stream().filter(skillResponseDTO -> skillResponseDTO.getId().equals(sk.getId())).findAny().get();
            if (!Objects.equals(sk.getName(), dto.getName()) || !Objects.equals(sk.getDescription(), dto.getDescription())) {
                return false;
            }
        }
        return true;
    }
}
