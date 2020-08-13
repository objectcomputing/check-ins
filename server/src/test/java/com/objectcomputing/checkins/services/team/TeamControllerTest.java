package com.objectcomputing.checkins.services.team;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MicronautTest
public class TeamControllerTest {

    private static String testName = "testName";
    private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
        put("name", testName);
    }};
    TeamRepository mockTeamRepository = mock(TeamRepository.class);
    Team mockTeam = mock(Team.class);
    @Inject
    @Client("/services/team")
    private HttpClient client;

    @BeforeEach
    void setup() {
        reset(mockTeamRepository);
        reset(mockTeam);
    }

    @Test
    public void testFindNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99").basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testFindNonExistingEndpointReturnsNotFound() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.GET("/bar?order=foo").basicAuth(MEMBER_ROLE, MEMBER_ROLE)));

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By Name - when no user data exists
    @Test
    public void testGetFindByNameReturnsEmptyBody() {

        String testName = "testName";
        Team team = new Team();
        List<Team> result = new ArrayList<Team>();
        result.add(team);

        when(mockTeamRepository.findByName(testName)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?name=%s", testName)).basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    // test Find All
    @Test
    public void testGetFindAll() {

        Team testTeam = new Team("testName", "testDescription");
        final HttpResponse<?> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", testTeam)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE));


        HttpRequest requestFindAll = HttpRequest.GET(String.format(""))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<Team> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockTeam.getClass()));
        assertTrue(responseFindAll.size() > 0);
    }

    // test Find By Name
    @Test
    public void testGetFindByName() {
        Team testTeam = new Team("testName", "testDescription");
        HttpResponse<Team> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", testTeam)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE), Team.class);

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?name=%s", testName))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<Team> responseFindByName = client.toBlocking().retrieve(requestFindByName, Argument.of(List.class, mockTeam.getClass()));
        assertEquals(testName, responseFindByName.get(0).getName());

    }


    // POST - Valid Body
    @Test
    public void testPostSave() {

        Team testTeam = new Team("testName", "testDescription");

        when(mockTeamRepository.save(testTeam)).thenReturn(testTeam);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("", testTeam)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        Team testTeam = new Team("testName", "testDescription");
        HttpResponse<Team> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", testTeam)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE), Team.class);

        UUID testUuid = responseFromPost.body().getUuid();
        testTeam.setUuid(testUuid);
        //Team testTeam = new Team("testName","testDescription");

        when(mockTeamRepository.update(testTeam)).thenReturn(testTeam);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.PUT("", testTeam)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        Team testTeam = new Team();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testTeam)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", fakeBody)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

}