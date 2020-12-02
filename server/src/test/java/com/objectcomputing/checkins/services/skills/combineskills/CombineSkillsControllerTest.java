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

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

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

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);

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

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

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

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);

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

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        Skill skill = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

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

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);

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

}