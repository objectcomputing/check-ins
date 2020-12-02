package com.objectcomputing.checkins.services.skills.combineskills;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillCreateDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class CombineSkillsControllerTest extends TestContainersSuite
        implements MemberProfileFixture, SkillFixture, MemberSkillFixture {

    @Inject
    @Client("/services/skill/combine")
    private HttpClient client;

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Test
    public void testPOSTCombine2SkillsIntoOne() {
        // set up 2 members
        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        // set up 2-3 skills
        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        // put a skill or 2 in each members memberskills
        // person 1 - 2 skills
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberProfile1.getId());
        memberSkillCreateDTO.setSkillid(skill.getId());
        memberSkillCreateDTO.setSkilllevel(skillLevel);
        memberSkillCreateDTO.setLastuseddate(lastUsedDate);
        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkillCreateDTO memberSkillCreateDTO1 = new MemberSkillCreateDTO();
        memberSkillCreateDTO1.setMemberid(memberProfile1.getId());
        memberSkillCreateDTO1.setSkillid(skill2.getId());
        memberSkillCreateDTO1.setSkilllevel(skillLevel);
        memberSkillCreateDTO1.setLastuseddate(lastUsedDate);
        skillLevel = "Near Guru";
        lastUsedDate = LocalDate.now();
        MemberSkillCreateDTO memberSkillCreateDTO2 = new MemberSkillCreateDTO();
        memberSkillCreateDTO2.setMemberid(memberProfile2.getId());
        memberSkillCreateDTO2.setSkillid(skill.getId());
        memberSkillCreateDTO2.setSkilllevel(skillLevel);
        memberSkillCreateDTO2.setLastuseddate(lastUsedDate);
        // grab the ids from the new skills
        // create a combineskillsDTO object with a new name, desc, and two of the skills
        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);
        // call combineskills
        // see if skills are changed for each member
        // see if old skill is deleted

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        Skill returnedSkill = response.body();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(combineSkillsDTO.getName(), response.body().getName());
        assertEquals(returnedSkill.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), returnedSkill.getId()), response.getHeaders().get("location"));

    }

    @Test
    public void testPOSTCombine2SkillsIntoOneNonAdmin() {
        // set up 2 members
        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        // set up 2-3 skills
        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        // put a skill or 2 in each members memberskills
        // person 1 - 2 skills
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberProfile1.getId());
        memberSkillCreateDTO.setSkillid(skill.getId());
        memberSkillCreateDTO.setSkilllevel(skillLevel);
        memberSkillCreateDTO.setLastuseddate(lastUsedDate);
        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkillCreateDTO memberSkillCreateDTO1 = new MemberSkillCreateDTO();
        memberSkillCreateDTO1.setMemberid(memberProfile1.getId());
        memberSkillCreateDTO1.setSkillid(skill2.getId());
        memberSkillCreateDTO1.setSkilllevel(skillLevel);
        memberSkillCreateDTO1.setLastuseddate(lastUsedDate);
        skillLevel = "Near Guru";
        lastUsedDate = LocalDate.now();
        MemberSkillCreateDTO memberSkillCreateDTO2 = new MemberSkillCreateDTO();
        memberSkillCreateDTO2.setMemberid(memberProfile2.getId());
        memberSkillCreateDTO2.setSkillid(skill.getId());
        memberSkillCreateDTO2.setSkilllevel(skillLevel);
        memberSkillCreateDTO2.setLastuseddate(lastUsedDate);
        // grab the ids from the new skills
        // create a combineskillsDTO object with a new name, desc, and two of the skills
        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);
        // call combineskills
        // see if skills are changed for each member
        // see if old skill is deleted

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    public void testPOSTCombine2SkillsIntoOneCheckMemberSkills() {
        // set up 2 members
        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        // set up 2-3 skills
        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        // put a skill or 2 in each members memberskills
        // person 1 - 2 skills
        MemberSkillCreateDTO memberSkillCreateDTO = new MemberSkillCreateDTO();
        memberSkillCreateDTO.setMemberid(memberProfile1.getId());
        memberSkillCreateDTO.setSkillid(skill.getId());
        memberSkillCreateDTO.setSkilllevel(skillLevel);
        memberSkillCreateDTO.setLastuseddate(lastUsedDate);
        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkillCreateDTO memberSkillCreateDTO1 = new MemberSkillCreateDTO();
        memberSkillCreateDTO1.setMemberid(memberProfile1.getId());
        memberSkillCreateDTO1.setSkillid(skill2.getId());
        memberSkillCreateDTO1.setSkilllevel(skillLevel);
        memberSkillCreateDTO1.setLastuseddate(lastUsedDate);
        skillLevel = "Near Guru";
        lastUsedDate = LocalDate.now();
        MemberSkillCreateDTO memberSkillCreateDTO2 = new MemberSkillCreateDTO();
        memberSkillCreateDTO2.setMemberid(memberProfile2.getId());
        memberSkillCreateDTO2.setSkillid(skill.getId());
        memberSkillCreateDTO2.setSkilllevel(skillLevel);
        memberSkillCreateDTO2.setLastuseddate(lastUsedDate);
        // grab the ids from the new skills
        // create a combineskillsDTO object with a new name, desc, and two of the skills
        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);
        // call combineskills
        // see if skills are changed for each member
        // see if old skill is deleted

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        Skill returnedSkill = response.body();

//        MemberSkill memberSkillForMember1 =

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(combineSkillsDTO.getName(), response.body().getName());
        assertEquals(returnedSkill.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), returnedSkill.getId()), response.getHeaders().get("location"));

//        assertEquals(memberSkill.getSkilllevel(), skillLevel);
//        assertEquals(memberSkill.getLastuseddate(), lastUsedDate);

    }

//    @Test
//    public void testGETFindByValueName() {
//
//        Skill skill = createADefaultSkill();
//        final HttpRequest<Object> request = HttpRequest.
//                GET(String.format("/?name=%s", encodeValue(skill.getName()))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//
//        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));
//
//        assertEquals(Set.of(skill), response.body());
//        assertEquals(HttpStatus.OK,response.getStatus());
//
//    }
//
//    @Test
//    public void testGETFindByValuePending() {
//
//        Skill skill = createADefaultSkill();
//        final HttpRequest<Object> request = HttpRequest.
//                GET(String.format("/?pending=%s", encodeValue(String.valueOf(skill.isPending())))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//
//        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));
//
//        assertEquals(Set.of(skill), response.body());
//        assertEquals(HttpStatus.OK,response.getStatus());
//
//    }
//
//    @Test
//    public void testGETGetByIdHappyPath() {
//
//        Skill skill = createADefaultSkill();
//
//        final HttpRequest<Object> request = HttpRequest.
//                GET(String.format("/%s", skill.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//
//        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);
//
//        assertEquals(skill, response.body());
//        assertEquals(HttpStatus.OK,response.getStatus());
//
//    }
//
//    @Test
//    public void testGETGetByIdNotFound() {
//
//        final HttpRequest<Object> request = HttpRequest.
//                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());
//
//    }
//
//    @Test
//    public void testPOSTCreateASkill() {
//
//        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
//        skillCreateDTO.setName("reincarnation");
//        skillCreateDTO.setPending(true);
//        skillCreateDTO.setExtraneous(true);
//        skillCreateDTO.setDescription("Bring back from the dead");
//
//        final HttpRequest<SkillCreateDTO> request = HttpRequest.
//                POST("/", skillCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//        final HttpResponse<Skill> response = client.toBlocking().exchange(request,Skill.class);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED,response.getStatus());
//        assertEquals(skillCreateDTO.getName(), response.body().getName());
//        assertEquals(skillCreateDTO.isExtraneous(), response.body().isExtraneous());
//        assertEquals(skillCreateDTO.getDescription(), response.body().getDescription());
//        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), response.getHeaders().get("location"));
//    }
//
//    @Test
//    public void testPOSTCreateASkillAlreadyExists() {
//
//        Skill skill = createADefaultSkill();
//        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
//        skillCreateDTO.setName(skill.getName());
//        skillCreateDTO.setPending(skill.isPending());
//
//        final HttpRequest<SkillCreateDTO> request = HttpRequest.
//                POST("/", skillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        final HttpResponse<Skill> response = client.toBlocking().exchange(request,Skill.class);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED,response.getStatus());
//        assertEquals(skillCreateDTO.getName(), response.body().getName());
//        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), response.getHeaders().get("location"));
//
//
//    }
//
//    @Test
//    public void testPOSTCreateASkillAlreadyExistsWhenPending() {
//
//        Skill skill = createADefaultSkill();
//        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
//        skillCreateDTO.setName(skill.getName());
//        skillCreateDTO.setPending(false);
//
//        final HttpRequest<SkillCreateDTO> request = HttpRequest.
//                POST("/", skillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.CONFLICT,responseException.getStatus());
//
//    }
//    @Test
//    public void testPOSTCreateANullSkill() {
//
//        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
//
//        final HttpRequest<SkillCreateDTO> request = HttpRequest.
//                POST("/", skillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());
//
//    }
//
//    @Test
//    public void testPUTUpdateSkill() {
//
//        Skill skill = createADefaultSkill();
//
//        final HttpRequest<Skill> request = HttpRequest.PUT("/", skill)
//                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);
//
//        assertEquals(skill, response.body());
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals(String.format("%s/%s", request.getPath(), skill.getId()),
//                response.getHeaders().get("location"));
//    }
//
//    @Test
//    public void testPUTUpdateNonexistentSkill() {
//
//        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
//        skillCreateDTO.setName("reincarnation");
//        skillCreateDTO.setPending(true);
//
//        final HttpRequest<SkillCreateDTO> request = HttpRequest.
//                PUT("/", skillCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());
//
//    }
//
//    @Test
//    public void testPUTUpdateNullSkill() {
//
//        final HttpRequest<String> request = HttpRequest.PUT("","").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());
//
//    }
//
//    @Test
//    void deleteMemberSkillAsAdmin() {
//
//        Skill skill = createADefaultSkill();
//
//        final HttpRequest<Object> request = HttpRequest.
//                DELETE(String.format("/%s", skill.getId())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
//
//        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//
//    }
//
//    @Test
//    void deleteMemberSkillNotAsAdmin() {
//
//        Skill skill = createADefaultSkill();
//
//        final HttpRequest<Object> request = HttpRequest.
//                DELETE(String.format("/%s", skill.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(request, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.FORBIDDEN,responseException.getStatus());
//
//    }


}