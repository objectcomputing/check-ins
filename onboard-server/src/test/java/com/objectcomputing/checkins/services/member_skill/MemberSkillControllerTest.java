package com.objectcomputing.checkins.services.member_skill;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemberSkillControllerTest extends TestContainersSuite implements MemberProfileFixture, SkillFixture, MemberSkillFixture {

    @Inject
    @Client("/services/member-skills")
    HttpClient client;


    @Test
    void testCreateAMemberSkill() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill() ;
        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberProfile.getId());
        memberSkillCreateDTO.setSkillid(skill.getId());
        memberSkillCreateDTO.setSkilllevel(skillLevel);
        memberSkillCreateDTO.setLastuseddate(lastUsedDate);


        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberSkill> response = client.toBlocking().exchange(request, MemberSkill.class);

        MemberSkill memberSkill = response.body();

        assertEquals(memberSkill, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), memberSkill.getId()), response.getHeaders().get("location"));
        assertEquals(memberSkill.getSkilllevel(), skillLevel);
        assertEquals(memberSkill.getLastuseddate(), lastUsedDate);
    }

    @Test
    void testCreateAMemberSkillWithNullableFieldsNull() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill() ;

        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberProfile.getId());
        memberSkillCreateDTO.setSkillid(skill.getId());
        memberSkillCreateDTO.setSkilllevel(null);
        memberSkillCreateDTO.setLastuseddate(null);


        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberSkill> response = client.toBlocking().exchange(request, MemberSkill.class);

        MemberSkill memberSkill = response.body();

        assertEquals(memberSkill, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), memberSkill.getId()), response.getHeaders().get("location"));
        assertEquals(memberSkill.getSkilllevel(), null);
        assertEquals(memberSkill.getLastuseddate(), null);
    }

    @Test
    void testCreateAnInvalidMemberSkill() {
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();

        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("memberSkill.memberid: must not be null", errorList.get(0));
        assertEquals("memberSkill.skillid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullMemberSkill() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [memberSkill] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void createAMemeberSkillForExistingMemberProfileO() {
        Skill skill = createADefaultSkill();

        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(UUID.randomUUID());
        memberSkillCreateDTO.setSkillid(skill.getId());

        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member Profile %s doesn't exist",memberSkillCreateDTO.getMemberid()),error);

    }

    @Test
    void createAMemeberSkillForExistingSkill() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberProfile.getId());
        memberSkillCreateDTO.setSkillid(UUID.randomUUID());

        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Skill %s doesn't exist",memberSkillCreateDTO.getSkillid()),error);

    }

    @Test
    void createAMemeberSkillForExistingSkillAndMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = "";
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberSkill.getMemberid());
        memberSkillCreateDTO.setSkillid(memberSkill.getSkillid());

        final HttpRequest<MemberSkillCreateDTO> request = HttpRequest.POST("", memberSkillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s already has this skill %s",memberSkillCreateDTO.getMemberid(),memberSkillCreateDTO.getSkillid()),error);

    }


    @Test
    void deleteMemberSkillAsAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = "";
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<Object> request = HttpRequest.DELETE(memberSkill.getId().toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteMemberSkillNotAsAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = "";
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<Object> request = HttpRequest.DELETE(memberSkill.getId().toString()).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadAllMemberSkills() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?memberid=%s&skillid=%s","","")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberSkill>> response = client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class));

        assertEquals(Set.of(memberSkill), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadMemberSkill() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", memberSkill.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberSkill> response = client.toBlocking().exchange(request, MemberSkill.class);

        assertEquals(memberSkill, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadMemberSkillNotFound() {

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, MemberSkill.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindMemberSkills() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s&skillid=%s", memberSkill.getMemberid(),
                memberSkill.getSkillid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberSkill>> response = client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class));

        assertEquals(Set.of(memberSkill), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindMemberSkillsByMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", memberSkill.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberSkill>> response = client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class));

        assertEquals(Set.of(memberSkill), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindMemberSkillsBySkillId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?skillid=%s", memberSkill.getSkillid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberSkill>> response = client.toBlocking().exchange(request, Argument.setOf(MemberSkill.class));

        assertEquals(Set.of(memberSkill), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    public void testPUTUpdateMemberSkill() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);

        final HttpRequest<?> request = HttpRequest.PUT("/", memberSkill).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<MemberSkill> response = client.toBlocking().exchange(request, MemberSkill.class);

        assertEquals(memberSkill, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), memberSkill.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNullMemberSkill() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [memberSkill] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    public void testPUTUpdateNonexistentMemberSkill() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        Skill skill = createADefaultSkill();
        String skillLevel = null;
        LocalDate skillDate = LocalDate.now();

        MemberSkill memberSkill = createMemberSkill(memberProfile,skill, skillLevel, skillDate);
        memberSkill.setId(UUID.randomUUID());

        final HttpRequest<MemberSkill> request = HttpRequest.PUT("/", memberSkill).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals(String.format("MemberSkill %s does not exist, cannot update",memberSkill.getId()), errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

}
