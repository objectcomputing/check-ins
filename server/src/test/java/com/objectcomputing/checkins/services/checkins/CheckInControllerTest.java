package com.objectcomputing.checkins.services.checkins;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileController;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest
public class CheckInControllerTest {

    @Inject
    @Client("/check-in")
    private HttpClient client;

    @Inject
    MemberProfileController memberProfileController;

    CheckInRepository mockCheckInRepository = mock(CheckInRepository.class);
    CheckIn mockCheckIn = mock(CheckIn.class);

    private static UUID testId;
    private static UUID testTeamMemberId;
    private static UUID testPdlId;
    private static LocalDate testDate = LocalDate.now();
    private static String testQuarter = "Q2";
    private static String testYear = "2020";
    private static boolean isDataSetupForTest = false;

    @BeforeAll
    void setupMemberProfileRecord() {
    
        // setup a record in Member-Profile to satisfy foreign key constraint
        if(memberProfileController != null) {
            MemberProfile testMemberProfile = new MemberProfile("TestName", 
                                                                "TestRole", 
                                                                null, 
                                                                "TestLocation", 
                                                                "TestEmail", 
                                                                "TestInsperityId", 
                                                                LocalDate.now(), 
                                                                "TestBio");

            final HttpResponse<?> response = memberProfileController.save(testMemberProfile);
            assertEquals(HttpStatus.CREATED, response.getStatus());
            assertNotNull(response.body());
            testTeamMemberId = ((MemberProfile) response.body()).getUuid();
            testPdlId = testTeamMemberId;
        }
    }
    
    @BeforeEach
    void setup() {
        reset(mockCheckInRepository);
        reset(mockCheckIn);
    }

    @Test
    public void testFindNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By TeamMemberId - when no user data exists
    @Test
    public void testGetFindByTeamMemberIdReturnsEmptyBody() {

        UUID testTeamMemberId = UUID.randomUUID();
        CheckIn checkin = new CheckIn();
        List<CheckIn> result = new ArrayList<CheckIn>();
        result.add(checkin);

        when(mockCheckInRepository.findByTeamMemberId(testTeamMemberId)).thenReturn(result);

        HttpRequest request = HttpRequest.GET(String.format("/?teamMemberId=%s", testTeamMemberId));
        List<CheckIn> response = client.toBlocking().retrieve(request, Argument.of(List.class, mockCheckIn.getClass()));

        assertEquals(0, response.size());
    }

    // Find By TargetYearAndTargetQtr - when no user data exists
    @Test
    public void testGetFindByTargetYearAndTargetQtrReturnsEmptyBody() {

        String testTargetYear = "2019";
        String testTargetQuarter = "Q4";
        CheckIn checkin = new CheckIn();
        List<CheckIn> result = new ArrayList<CheckIn>();
        result.add(checkin);

        when(mockCheckInRepository.findByTargetYearAndTargetQtr(testTargetYear, testTargetQuarter)).thenReturn(result);

        HttpRequest request = HttpRequest.GET(String.format("/?targetYear=%s&targetQtr=%s", testTargetYear, testTargetQuarter));
        List<CheckIn> response = client.toBlocking().retrieve(request, Argument.of(List.class, mockCheckIn.getClass()));

        assertEquals(0, response.size());
    }

    // Find By PdlId - when no user data exists
    @Test
    public void testGetFindByPdlIdReturnsEmptyBody() {

        UUID testId = UUID.randomUUID();
        CheckIn checkin = new CheckIn();
        List<CheckIn> result = new ArrayList<CheckIn>();
        result.add(checkin);

        when(mockCheckInRepository.findByPdlId(testId)).thenReturn(result);

        HttpRequest request = HttpRequest.GET(String.format("/?pdlId=%s", testId));
        List<CheckIn> response = client.toBlocking().retrieve(request, Argument.of(List.class, mockCheckIn.getClass()));

        assertEquals(0, response.size());
    }

    // test Find All
    @Test
    public void testGetFindAll() {

        setupTestData();

        HttpRequest requestFindAll = HttpRequest.GET("");
        List<CheckIn> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockCheckIn.getClass()));

        assertEquals(1, responseFindAll.size());
        assertEquals(testTeamMemberId, responseFindAll.get(0).getTeamMemberId());
        assertEquals(testPdlId, responseFindAll.get(0).getPdlId());
    }

    // test Find By TeamMemberId
    @Test
    public void testGetFindByTeamMemberId() {

        setupTestData();

        HttpRequest requestFindByTeamMemberId = HttpRequest.GET(String.format("/?teamMemberId=%s", testTeamMemberId));
        List<CheckIn> responseFindByName = client.toBlocking().retrieve(requestFindByTeamMemberId, Argument.of(List.class, mockCheckIn.getClass()));

        assertEquals(1, responseFindByName.size());
        assertEquals(testTeamMemberId, responseFindByName.get(0).getTeamMemberId());
        assertEquals(testPdlId, responseFindByName.get(0).getPdlId());
    }

    // test Find By TargetYearAndTargetQtr
    @Test
    public void testGetFindByTargetYearAndTargetQtr() {

        setupTestData();

        HttpRequest requestFindByTargetYearAndTargetQtr = HttpRequest.GET(String.format("/?targetYear=%s&targetQtr=%s", testYear, testQuarter));
        List<CheckIn> responseFindByTargetYearAndTargetQtr = client.toBlocking().retrieve(requestFindByTargetYearAndTargetQtr, Argument.of(List.class, mockCheckIn.getClass()));
        
        assertEquals(1, responseFindByTargetYearAndTargetQtr.size());
        assertEquals(testTeamMemberId, responseFindByTargetYearAndTargetQtr.get(0).getTeamMemberId());
        assertEquals(testYear, responseFindByTargetYearAndTargetQtr.get(0).getTargetYear());
        assertEquals(testQuarter, responseFindByTargetYearAndTargetQtr.get(0).getTargetQtr());
    }

    // test Find By PdlId
    @Test
    public void testGetFindByPdlId() {

        setupTestData();

        HttpRequest requestFindByPdlId = HttpRequest.GET(String.format("/?pdlId=%s", testPdlId));
        List<CheckIn> responseFindByPdlId = client.toBlocking().retrieve(requestFindByPdlId, Argument.of(List.class, mockCheckIn.getClass()));  
        assertEquals(1, responseFindByPdlId.size());
        assertEquals(testTeamMemberId, responseFindByPdlId.get(0).getTeamMemberId());
        assertEquals(testPdlId, responseFindByPdlId.get(0).getPdlId());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        CheckIn testCheckin = new CheckIn(testTeamMemberId, testPdlId, testDate, "Q3", "2025");

        final HttpResponse<CheckIn> response = client.toBlocking().exchange(HttpRequest.POST("", testCheckin), CheckIn.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertNotNull(response.body().getId());
        assertEquals(testTeamMemberId, response.body().getTeamMemberId());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        setupTestData();

        CheckIn testCheckInPut = new CheckIn(testTeamMemberId, testPdlId, testDate, "Q4", "2021");
        testCheckInPut.setId(testId);

        final HttpResponse<CheckIn> responseFromPut = client.toBlocking().exchange(HttpRequest.PUT("", testCheckInPut), CheckIn.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatus());
        assertNotNull(responseFromPut.body());
        assertEquals(testId, responseFromPut.body().getId());
        assertEquals("Q4", responseFromPut.body().getTargetQtr());
        assertEquals("2021", responseFromPut.body().getTargetYear());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        CheckIn testCheckIn = new CheckIn();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testCheckIn));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        CheckIn testCheckin = new CheckIn(testTeamMemberId, testPdlId, testDate, testQuarter, testYear);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testCheckin));
        });

        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private void setupTestData() {
        if(!isDataSetupForTest) {
            CheckIn testCheckin = new CheckIn(testTeamMemberId, testPdlId, testDate, testQuarter, testYear);
            final HttpResponse<CheckIn> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", testCheckin), CheckIn.class);

            assertEquals(HttpStatus.CREATED, responseFromPost.getStatus());
            assertNotNull(responseFromPost.body());
            testId = responseFromPost.body().getId();

            isDataSetupForTest = true;
        }
    }

}