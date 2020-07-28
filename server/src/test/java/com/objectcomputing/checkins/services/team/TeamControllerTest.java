// package com.objectcomputing.checkins.services.team;

// import static org.junit.Assert.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.reset;
// import static org.mockito.Mockito.when;

// import java.sql.Date;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;

// import javax.inject.Inject;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import io.micronaut.core.type.Argument;
// import io.micronaut.http.HttpRequest;
// import io.micronaut.http.HttpResponse;
// import io.micronaut.http.HttpStatus;
// import io.micronaut.http.client.HttpClient;
// import io.micronaut.http.client.annotation.Client;
// import io.micronaut.http.client.exceptions.HttpClientResponseException;
// import io.micronaut.test.annotation.MicronautTest;

// @MicronautTest
// public class TeamControllerTest {

//     @Inject
//     @Client("/team")
//     private HttpClient client;

//     TeamRepository mockTeamRepository = mock(TeamRepository.class);
//     Team mockTeam = mock(Team.class);

//     private static Date testDate = new Date(System.currentTimeMillis());
//     private static String testUser = "testName";
//     private static String testRole = "testRole";
//     private static UUID testPdlId = UUID.randomUUID();
//     private static boolean isDataSetupForGetTest = false;

//     private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
//         put("name", testUser);
//         put("role", testRole);
//         put("pdlId", testPdlId);
//         put("location", "testLocation");
//         put("workEmail", "testEmail");
//         put("insperityId", "testInsperityId");
//         put("startDate", testDate);
//         put("bioText", "testBio");
//     }};

//     @BeforeEach
//     void setup() {
//         reset(mockTeamRepository);
//         reset(mockTeam);
//     }

//     @Test
//     public void testFindNonExistingEndpointReturns404() {
//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.GET("/99"));
//         });

//         assertNotNull(thrown.getResponse());
//         assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
//     }

//     @Test
//     public void testFindNonExistingEndpointReturnsNotFound() {
//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.GET("/bar?order=foo"));
//         });

//         assertNotNull(thrown.getResponse());
//         assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
//     }

//     // Find By Name - when no user data exists
//     @Test
//     public void testGetFindByNameReturnsEmptyBody() {

//         String testUser = "testUser";
//         Team team = new Team();
//         List<Team> result = new ArrayList<Team>();
//         result.add(team);

//         when(mockTeamRepository.findByName("testUser")).thenReturn(result);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?name=%s", testUser)));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertEquals(2, response.getContentLength());
//     }

//     // Find By Role - when no user data exists
//     @Test
//     public void testGetFindByRoleReturnsEmptyBody() {

//         String testRole = "test";
//         Team team = new Team();
//         List<Team> result = new ArrayList<Team>();
//         result.add(team);

//         when(mockTeamRepository.findByRole("test")).thenReturn(result);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?role=%s", testRole)));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertEquals(2, response.getContentLength());
//     }

//     // Find By PdlId - when no user data exists
//     @Test
//     public void testGetFindByPdlIdReturnsEmptyBody() {

//         UUID testUUuid = UUID.randomUUID();
//         Team team = new Team();
//         List<Team> result = new ArrayList<Team>();
//         result.add(team);

//         when(mockTeamRepository.findByPdlId(testUUuid)).thenReturn(result);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?pdlId=%s", testUUuid)));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertEquals(2, response.getContentLength());
//     }

//     // test Find All
//     @Test
//     public void testGetFindAll() {

//         if(!isDataSetupForGetTest){
//             setupTestData();
//             isDataSetupForGetTest = true;
//         }

//         HttpRequest requestFindAll = HttpRequest.GET(String.format(""));
//         List<Team> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockTeam.getClass()));  
//         assertEquals(1, responseFindAll.size());
//         assertEquals(testUser, responseFindAll.get(0).getName());
//         assertEquals(testRole, responseFindAll.get(0).getRole());
//     }

//     // test Find By Name
//     @Test
//     public void testGetFindByName() {

//         if(!isDataSetupForGetTest){
//             setupTestData();
//             isDataSetupForGetTest = true;
//         }

//         HttpRequest requestFindByName = HttpRequest.GET(String.format("/?name=%s", testUser));
//         List<Team> responseFindByName = client.toBlocking().retrieve(requestFindByName, Argument.of(List.class, mockTeam.getClass()));  
//         assertEquals(1, responseFindByName.size());
//         assertEquals(testUser, responseFindByName.get(0).getName());
//         assertEquals(testRole, responseFindByName.get(0).getRole());
//     }

//     // test Find By Role
//     @Test
//     public void testGetFindByRole() {

//         if(!isDataSetupForGetTest){
//             setupTestData();
//             isDataSetupForGetTest = true;
//         }

//         HttpRequest requestFindByRole = HttpRequest.GET(String.format("/?role=%s", testRole));
//         List<Team> responseFindByRole = client.toBlocking().retrieve(requestFindByRole, Argument.of(List.class, mockTeam.getClass()));  
//         assertEquals(1, responseFindByRole.size());
//         assertEquals(testUser, responseFindByRole.get(0).getName());
//         assertEquals(testRole, responseFindByRole.get(0).getRole());
//     }

//     // test Find By PdlId
//     @Test
//     public void testGetFindByPdlId() {

//         if(!isDataSetupForGetTest){
//             setupTestData();
//             isDataSetupForGetTest = true;
//         }

//         HttpRequest requestFindByPdlId = HttpRequest.GET(String.format("/?pdlId=%s", testPdlId));
//         List<Team> responseFindByPdlId = client.toBlocking().retrieve(requestFindByPdlId, Argument.of(List.class, mockTeam.getClass()));  
//         assertEquals(1, responseFindByPdlId.size());
//         assertEquals(testUser, responseFindByPdlId.get(0).getName());
//         assertEquals(testRole, responseFindByPdlId.get(0).getRole());
//         assertEquals(testPdlId, responseFindByPdlId.get(0).getPdlId());
//     }

//     // POST - Valid Body
//     @Test
//     public void testPostSave() {

//         Team testTeam = new Team("testName", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");

//         when(mockTeamRepository.save(testTeam)).thenReturn(testTeam);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
//         assertEquals(HttpStatus.CREATED, response.getStatus());
//         assertNotNull(response.getContentLength());
//     }

//     // POST - Invalid call
//     @Test
//     public void testPostNonExistingEndpointReturns404() {

//         Team testTeam = new Team();
//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.POST("/99", testTeam));
//         });

//         assertNotNull(thrown.getResponse());
//         assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
//     }

//     // PUT - Valid Body
//     @Test
//     public void testPutUpdate() {

//         UUID testId = UUID.randomUUID();
//         Team testTeam = new Team("Name", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");
//         testTeam.setUuid(testId);

//         Map<String, Object> fakeBody = new HashMap<String, Object>() {{
//             put("uuid", testId);
//             put("name", "updatedName");
//             put("role", "testRole");
//             put("pdlId", UUID.randomUUID());
//             put("location", "testLocation");
//             put("workEmail", "testEmail");
//             put("insperityId", "testInsperityId");
//             put("startDate", testDate);
//             put("bioText", "testBio");
//         }};

//         when(mockTeamRepository.update(testTeam)).thenReturn(testTeam);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotNull(response.getContentLength());
//     }

//     // PUT - Request with empty body
//     @Test
//     public void testPutUpdateForEmptyInput() {
//         Team testTeam = new Team();
//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.PUT("", testTeam));
//         });
//         assertNotNull(thrown);
//         assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//     }

//     // PUT - Request with invalid body - missing ID
//     @Test
//     public void testPutUpdateWithMissingField() {

//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
//         });
//         assertNotNull(thrown);
//         assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//     }

//     private void setupTestData() {
//         client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
//     }
// }