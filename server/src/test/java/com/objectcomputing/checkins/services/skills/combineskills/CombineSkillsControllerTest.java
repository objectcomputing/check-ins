package com.objectcomputing.checkins.services.skills.combineskills;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
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
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class CombineSkillsControllerTest extends TestContainersSuite
        implements MemberProfileFixture, SkillFixture, MemberSkillFixture {

    @Inject
    @Client("/services/skill/combine")
    private HttpClient client;

    @Inject
    @Client("/services/skill")
    private HttpClient skillClient;

    @Inject
    @Client("/services/member-skill")
    private HttpClient memberSkillClient;

    private final SkillServices skillServices;

    public CombineSkillsControllerTest(SkillServices skillServices1) {
        this.skillServices = skillServices1;
    }

    @Test
    public void testPOSTCombine2SkillsIntoOne() {

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        Skill skill1 = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        MemberSkill memberSkill1 = createMemberSkill(memberProfile1,skill1, skillLevel, lastUsedDate);

        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkill memberSkill2 = createMemberSkill(memberProfile1,skill2, skillLevel, lastUsedDate);

        skillLevel = "Near Guru";
        lastUsedDate = LocalDate.now();

        MemberSkill memberSkill3 = createMemberSkill(memberProfile2,skill1, skillLevel, lastUsedDate);

        UUID[] skillsToCombineArray = new UUID[2];
        skillsToCombineArray[0] = skill1.getId();
        skillsToCombineArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", skillsToCombineArray);

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

        Skill skill1 = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        MemberSkill memberSkill1 = createMemberSkill(memberProfile1,skill1, skillLevel, lastUsedDate);

        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkill memberSkill2 = createMemberSkill(memberProfile1,skill2, skillLevel, lastUsedDate);

        skillLevel = "Near Guru";
        lastUsedDate = LocalDate.now();

        MemberSkill memberSkill3 = createMemberSkill(memberProfile2,skill1, skillLevel, lastUsedDate);

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill1.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("/", combineSkillsDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    public void testPOSTCombineNonExistingSkillNameAdmin() {

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = UUID.randomUUID();
        newSkillsArray[1] = UUID.randomUUID();
        CombineSkillsDTO combineSkillsDTO =
                new CombineSkillsDTO(null, "New Skill Desc", newSkillsArray);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("skill.name: must not be blank", error);

    }

    @Test
    public void testPOSTCombineBlankSkillNameAdmin() {

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = UUID.randomUUID();
        newSkillsArray[1] = UUID.randomUUID();
        CombineSkillsDTO combineSkillsDTO =
                new CombineSkillsDTO("", "New Skill Desc", newSkillsArray);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("skill.name: must not be blank", error);

    }

    @Test
    public void testPOSTCombineNonExistingSkillsAdmin() {

        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", null);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("skill.skillsToCombine: must not be null", error);

    }

    @Test
    public void testPOSTCombine2SkillsIntoOneCheckSkillsDeleted() {

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        Skill skill1 = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        MemberSkill memberSkill1 = createMemberSkill(memberProfile1,skill1, skillLevel, lastUsedDate);

        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkill memberSkill2 = createMemberSkill(memberProfile1,skill2, skillLevel, lastUsedDate);

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill1.getId();
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

        final MutableHttpRequest<Object> skillRequest = HttpRequest.GET(String.format("/%s", skill1.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> skillClient.toBlocking().exchange(skillRequest, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

        final MutableHttpRequest<Object>  skillRequest2 = HttpRequest.GET(String.format("/%s", skill2.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        responseException = assertThrows(HttpClientResponseException.class,
                () -> skillClient.toBlocking().exchange(skillRequest2, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    public void testPOSTCombine2SkillsIntoOneCheckMemberSkills() {

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        Skill skill1 = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        MemberSkill memberSkill1 = createMemberSkill(memberProfile1,skill1, skillLevel, lastUsedDate);

        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkill memberSkill2 = createMemberSkill(memberProfile1,skill2, skillLevel, lastUsedDate);

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill1.getId();
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
//
//        final MutableHttpRequest<Object> skillRequest = HttpRequest.GET(String.format("/%s", skill1.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> skillClient.toBlocking().exchange(skillRequest, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());
//
//        final MutableHttpRequest<Object>  skillRequest2 = HttpRequest.GET(String.format("/%s", skill2.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
//        responseException = assertThrows(HttpClientResponseException.class,
//                () -> skillClient.toBlocking().exchange(skillRequest2, Map.class));
//
//        assertNotNull(responseException.getResponse());
//        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

        final MutableHttpRequest<Object> memberSkillRequest = HttpRequest.GET(String.format("/%s", memberSkill1.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> memberSkillClient.toBlocking().exchange(memberSkillRequest, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

        final MutableHttpRequest<Object> memberSkillRequest2 = HttpRequest.GET(String.format("/%s", memberSkill2.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        responseException = assertThrows(HttpClientResponseException.class,
                () -> memberSkillClient.toBlocking().exchange(memberSkillRequest2, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

}