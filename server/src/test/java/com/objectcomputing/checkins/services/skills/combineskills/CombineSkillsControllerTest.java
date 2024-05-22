package com.objectcomputing.checkins.services.skills.combineskills;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CombineSkillsControllerTest extends TestContainersSuite
        implements MemberProfileFixture, RoleFixture, SkillFixture, MemberSkillFixture {

    @Inject
    @Client("/services/skills/combine")
    private HttpClient client;

    @Inject
    @Client("/services/skills")
    private HttpClient skillClient;

    @Inject
    @Client("/services/member-skills")
    private HttpClient memberSkillClient;

//    @Inject
//    private SkillServices skillServices;
//
//    public CombineSkillsControllerTest(SkillServices skillServices1) {
//        this.skillServices = skillServices1;
//    }

    @Test
    void testPOSTCombine2SkillsIntoOne() {
        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        MemberProfile memberProfileOfAdmin = createADefaultMemberProfileForPdl(memberProfile1);
        createAndAssignAdminRole(memberProfileOfAdmin);

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

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        Skill returnedSkill = response.body();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(combineSkillsDTO.getName(), response.body().getName());
        assertEquals(returnedSkill.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), returnedSkill.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testPOSTCombine2SkillsIntoOneNonAdmin() {

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
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("skill.name: must not be blank", error.asText());

    }

    @Test
    void testPOSTCombineBlankSkillNameAdmin() {

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = UUID.randomUUID();
        newSkillsArray[1] = UUID.randomUUID();
        CombineSkillsDTO combineSkillsDTO =
                new CombineSkillsDTO("", "New Skill Desc", newSkillsArray);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("skill.name: must not be blank", error.asText());

    }

    @Test
    void testPOSTCombineNonExistingSkillsAdmin() {

        CombineSkillsDTO combineSkillsDTO =
             new CombineSkillsDTO("New Skill", "New Skill Desc", null);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("skill.skillsToCombine: must not be null", error.asText());

    }

    @Test
    void testPOSTCombine2SkillsIntoOneCheckSkillsDeleted() {

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        MemberProfile memberProfileOfAdmin = createADefaultMemberProfileForPdl(memberProfile1);
        createAndAssignAdminRole(memberProfileOfAdmin);

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

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        Skill returnedSkill = response.body();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(combineSkillsDTO.getName(), response.body().getName());
        assertEquals(returnedSkill.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), returnedSkill.getId()), response.getHeaders().get("location"));

        final MutableHttpRequest<Object> skillRequest = HttpRequest.GET(String.format("/%s", skill1.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> skillClient.toBlocking().exchange(skillRequest, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

        final MutableHttpRequest<Object>  skillRequest2 = HttpRequest.GET(String.format("/%s", skill2.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        responseException = assertThrows(HttpClientResponseException.class,
                () -> skillClient.toBlocking().exchange(skillRequest2, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    void testPOSTCombine2SkillsIntoOneCheckMemberSkills() {

        MemberProfile memberProfile1 = createADefaultMemberProfile();
        MemberProfile memberProfile2 = createAnUnrelatedUser();

        MemberProfile memberProfileOfAdmin = createADefaultMemberProfileForPdl(memberProfile1);
        createAndAssignAdminRole(memberProfileOfAdmin);

        Skill skill1 = createADefaultSkill();
        Skill skill2 = createASecondarySkill();

        String skillLevel = "Guru";
        LocalDate lastUsedDate = LocalDate.now();

        MemberSkill memberSkill1 = createMemberSkill(memberProfile1,skill1, skillLevel, lastUsedDate);

        skillLevel = "Guru Plus";
        lastUsedDate = LocalDate.now();
        MemberSkill memberSkill2 = createMemberSkill(memberProfile1,skill2, skillLevel, lastUsedDate);

        skillLevel = "Plano Guru";
        lastUsedDate = LocalDate.now();
        MemberSkill memberSkill3 = createMemberSkill(memberProfile2,skill2, skillLevel, lastUsedDate);

        UUID[] newSkillsArray = new UUID[2];
        newSkillsArray[0] = skill1.getId();
        newSkillsArray[1] = skill2.getId();
        CombineSkillsDTO combineSkillsDTO =
                new CombineSkillsDTO("New Skill", "New Skill Desc", newSkillsArray);

        final MutableHttpRequest<CombineSkillsDTO> request = HttpRequest.POST("", combineSkillsDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        Skill returnedSkill = response.body();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(combineSkillsDTO.getName(), response.body().getName());
        assertEquals(returnedSkill.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), returnedSkill.getId()), response.getHeaders().get("location"));

        MutableHttpRequest<Object> memberSkillRequest = HttpRequest.GET(String.format("/?skillid=%s", skill1.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpResponse<Set<MemberSkill>> skillAssignments =  memberSkillClient.toBlocking().exchange(memberSkillRequest, Argument.setOf(MemberSkill.class));
        Set<MemberSkill> memberSkillSet = skillAssignments.body();
        assertEquals(Set.of(), memberSkillSet);

        memberSkillRequest = HttpRequest.GET(String.format("/?skillid=%s", skill2.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        skillAssignments =  memberSkillClient.toBlocking().exchange(memberSkillRequest, Argument.setOf(MemberSkill.class));
        memberSkillSet = skillAssignments.body();
        assertEquals(Set.of(), memberSkillSet);

    }

}