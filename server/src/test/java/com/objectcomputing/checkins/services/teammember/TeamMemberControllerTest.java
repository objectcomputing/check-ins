// package com.objectcomputing.checkins.services.teammember;

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
// public class TeamMemberControllerTest {

//     @Inject
//     @Client("/team-member")
//     private HttpClient client;

//     TeamMemberRepository mockTeamMemberRepository = mock(TeamMemberRepository.class);
//     TeamMember mockTeamMember = mock(TeamMember.class);

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
//         reset(mockTeamMemberRepository);
//         reset(mockTeamMember);
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
//         TeamMember teamMember = new TeamMember();
//         List<TeamMember> result = new ArrayList<TeamMember>();
//         result.add(teamMember);

//         when(mockTeamMemberRepository.findByName("testUser")).thenReturn(result);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?name=%s", testUser)));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertEquals(2, response.getContentLength());
//     }

//     // Find By Role - when no user data exists
//     @Test
//     public void testGetFindByRoleReturnsEmptyBody() {

//         String testRole = "test";
//         TeamMember teamMember = new TeamMember();
//         List<TeamMember> result = new ArrayList<TeamMember>();
//         result.add(teamMember);

//         when(mockTeamMemberRepository.findByRole("test")).thenReturn(result);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?role=%s", testRole)));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertEquals(2, response.getContentLength());
//     }

//     // Find By PdlId - when no user data exists
//     @Test
//     public void testGetFindByPdlIdReturnsEmptyBody() {

//         UUID testUUuid = UUID.randomUUID();
//         TeamMember teamMember = new TeamMember();
//         List<TeamMember> result = new ArrayList<TeamMember>();
//         result.add(teamMember);

//         when(mockTeamMemberRepository.findByPdlId(testUUuid)).thenReturn(result);

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
//         List<TeamMember> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockTeamMember.getClass()));  
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
//         List<TeamMember> responseFindByName = client.toBlocking().retrieve(requestFindByName, Argument.of(List.class, mockTeamMember.getClass()));  
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
//         List<TeamMember> responseFindByRole = client.toBlocking().retrieve(requestFindByRole, Argument.of(List.class, mockTeamMember.getClass()));  
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
//         List<TeamMember> responseFindByPdlId = client.toBlocking().retrieve(requestFindByPdlId, Argument.of(List.class, mockTeamMember.getClass()));  
//         assertEquals(1, responseFindByPdlId.size());
//         assertEquals(testUser, responseFindByPdlId.get(0).getName());
//         assertEquals(testRole, responseFindByPdlId.get(0).getRole());
//         assertEquals(testPdlId, responseFindByPdlId.get(0).getPdlId());
//     }

//     // POST - Valid Body
//     @Test
//     public void testPostSave() {

//         TeamMember testTeamMember = new TeamMember("testName", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");

//         when(mockTeamMemberRepository.save(testTeamMember)).thenReturn(testTeamMember);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
//         assertEquals(HttpStatus.CREATED, response.getStatus());
//         assertNotNull(response.getContentLength());
//     }

//     // POST - Invalid call
//     @Test
//     public void testPostNonExistingEndpointReturns404() {

//         TeamMember testTeamMember = new TeamMember();
//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.POST("/99", testTeamMember));
//         });

//         assertNotNull(thrown.getResponse());
//         assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
//     }

//     // PUT - Valid Body
//     @Test
//     public void testPutUpdate() {

//         UUID testId = UUID.randomUUID();
//         TeamMember testTeamMember = new TeamMember("Name", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");
//         testTeamMember.setUuid(testId);

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

//         when(mockTeamMemberRepository.update(testTeamMember)).thenReturn(testTeamMember);

//         final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotNull(response.getContentLength());
//     }

//     // PUT - Request with empty body
//     @Test
//     public void testPutUpdateForEmptyInput() {
//         TeamMember testTeamMember = new TeamMember();
//         HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//             client.toBlocking().exchange(HttpRequest.PUT("", testTeamMember));
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