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
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class SkillControllerTest extends TestContainersSuite implements SkillFixture {

    @Inject
    @Client("/services/skill")
    private HttpClient client;

    @Test
    public void testGETNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

//    @Test
//    public void testGETFindByNameReturnsEmptyBody() {
//
//        String testSkillName = "testSkill";
//        Skill skill = new Skill();
//        List<Skill> result = new ArrayList<Skill>();
//        result.add(skill);
//
//        when(mockSkillServices.findByValue("testSkill", null)).thenReturn(result);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest
//                        .GET(String.format("/?name=%s", testSkillName)));
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//    }

//    @Test
//    public void testGETFindByValue_Name() {
//
//        String testSkillName = "testSkill";
//        Skill skill = new Skill();
//        List<Skill> result = new ArrayList<Skill>();
//        result.add(skill);
//
//        when(mockSkillServices.findByValue("testSkill", null)).thenReturn(result);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest
//                        .GET(String.format("/?name=%s", testSkillName)));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        response.equals(skill);
//    }

//    @Test
//    public void testGETFindByValue_Pending() {
//
//        boolean testPending = false;
//        Skill skill = new Skill();
//        skill.setPending(testPending);
//        List<Skill> result = new ArrayList<Skill>();
//        result.add(skill);
//
//        when(mockSkillServices.findByValue("testSkill", testPending)).thenReturn(result);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest
//                        .GET(String.format("/?pending=%b", testPending)));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        response.equals(skill);
//
//    }

    @Test
    public void testGETGetById_HappyPath() {

        Skill skill = createADefaultSkill();

        final HttpRequest<?> request = HttpRequest.
                GET(String.format("/%s", skill.getSkillid())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

  /**/      final HttpResponse<Set<Skill>> response = client.toBlocking().exchange(request, Argument.setOf(Skill.class));

        assertEquals(Set.of(skill), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest
//                        .GET(String.format("/%s", skill.getSkillid())));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        response.equals(skill);
    }
//
//    @Test
//    public void testGETGetById_HappyPath() {
//
//        UUID uuid = UUID.fromString(fakeUuid);
//        Skill skill = new Skill();
//        skill.setSkillid(uuid);
//        skill.setName(testSkillName);
//        skill.setPending(pending);
//        List<Skill> result = new ArrayList<Skill>();
//        result.add(skill);
//
//        skill.setSkillid(UUID.fromString(fakeUuid));
//        when(mockSkillServices.readSkill(uuid)).thenReturn(skill);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest
//                        .GET(String.format("/%s", skill.getSkillid())));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        response.equals(skill);
//    }
//
//    @Test
//    public void testPOSTCreateASkill() {
//
//        Skill testSkill = new Skill("testName2", pending);
//
//        when(mockSkillServices.saveSkill(testSkill)).thenReturn(testSkill);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest.POST("/", testSkill));
//
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertNotNull(response.getContentLength());
//    }

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