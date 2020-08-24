package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillControllerTest extends TestContainersSuite implements SkillFixture {

    @Inject
    @Client("/services/skill")
    private HttpClient client;

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
                    .basicAuth(MEMBER_ROLE,MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGETFindByNameReturnsEmptyBody() {

        Skill skill = new Skill();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETFindByValueName() {

        Skill skill = createADefaultSkill();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue(skill.getName()))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(Set.of(skill), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETFindByValuePending() {

        Skill skill = createADefaultSkill();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?pending=%s", encodeValue(String.valueOf(skill.isPending())))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(Set.of(skill), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETGetByIdHappyPath() {

        Skill skill = createADefaultSkill();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", skill.getSkillid())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Skill> response = client.toBlocking().exchange(request, Skill.class);

        assertEquals(skill, response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testPOSTCreateASkill() {

        Skill skill = createADefaultSkill();
        SkillCreateDTO skillCreateDTO = new SkillCreateDTO();
        skillCreateDTO.setName(skill.getName());
        skillCreateDTO.setPending(skill.isPending());

        final HttpRequest<SkillCreateDTO> request = HttpRequest.
                POST("/", skillCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Skill> response = client.toBlocking().exchange(request,Skill.class);

//        assertEquals(skill, response.body());
//        assertEquals(HttpStatus.CREATED,response.getStatus());
//        assertEquals(skillCreateDTO.getName(), response.body().getName());

        // old =====================================================================

//        Skill testSkill = new Skill("testName2", pending);
//
//        when(mockSkillServices.saveSkill(testSkill)).thenReturn(testSkill);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest.POST("/", testSkill));
//
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertNotNull(response.getContentLength());
    }

//    @Test
//    public void testPOSTCreateASkill_Null_Skill() {
//
//        Skill testSkill = new Skill("testName", pending);
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.POST("/", testSkill));
//        });
//
//        assertNotNull(thrown.getResponse());
//        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
//    }
//
//    @Test
//    public void testPUTupdate() {
//
//        Skill fakeSkill = new Skill("fakeName", pending);
//        fakeSkill.setSkillid(UUID.fromString(fakeUuid));
//
//        when(mockSkillServices.update(fakeSkill)).thenReturn(fakeSkill);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest.PUT("/", fakeSkill));
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertNotNull(response.getContentLength());
//    }
//
//    @Test
//    public void testPUTupdate_nonexistent_skill() {
//
//        Skill fakeSkill = new Skill("fakeName", pending);
//        fakeSkill.setSkillid(UUID.fromString(fakeUuid));
//        Skill fakeSkill2 = new Skill("fakeName2", pending);
//        fakeSkill2.setSkillid(UUID.fromString(fakeUuid2));
//
//        when(mockSkillServices.update(fakeSkill2)).thenReturn(fakeSkill2);
//
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.POST("/", fakeSkill));
//        });
//
//        assertNotNull(thrown.getResponse());
//        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
//
//    }
//
//    @Test
//    public void testPUTupdate_null_skill() {
//
//        Skill fakeSkill = new Skill("fakeName", pending);
//        fakeSkill.setSkillid(UUID.fromString(fakeUuid));
//        Skill fakeSkill2 = new Skill("fakeName2", pending);
//        fakeSkill2.setSkillid(UUID.fromString(fakeUuid2));
//
//        when(mockSkillServices.update(fakeSkill2)).thenReturn(null);
//
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.PUT("/", fakeSkill));
//        });
//
//        assertNotNull(thrown.getResponse());
//
//    }


}