package com.objectcomputing.checkins.services.checkins;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class CheckInControllerTest {

    @Inject
    @Client("/check-in")
    private HttpClient client;

    CheckInRepository mockCheckInRepository = mock(CheckInRepository.class);
    CheckIn mockCheckIn = mock(CheckIn.class);

    private static UUID testMemberId = UUID.randomUUID();
    private static UUID testPdlId = UUID.randomUUID();
    private static LocalDate testDate = LocalDate.now();
    private static String testQuarter = "Q2";
    private static String testYear = "2020";
    private static boolean isDataSetupForGetTest = false;

    private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
        put("teamMemberId", testMemberId);
        put("pdlId", testPdlId);
        put("checkInDate", testDate);
        put("targetQtr", testQuarter);
        put("targetYear", testYear);
    }};

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

    @Test
    public void testFindNonExistingEndpointReturnsNotFound() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/bar?order=foo"));
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

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?teamMemberId=%s", testTeamMemberId)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
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

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?targetYear=%s&targetQtr=%s", testTargetYear, testTargetQuarter)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By PdlId - when no user data exists
    @Test
    public void testGetFindByPdlIdReturnsEmptyBody() {

        UUID testId = UUID.randomUUID();
        CheckIn checkin = new CheckIn();
        List<CheckIn> result = new ArrayList<CheckIn>();
        result.add(checkin);

        when(mockCheckInRepository.findByPdlId(testId)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?pdlId=%s", testId)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // test Find All
    @Test
    public void testGetFindAll() {

        if(!isDataSetupForGetTest){
            setupTestData();
            isDataSetupForGetTest = true;
        }

        HttpRequest requestFindAll = HttpRequest.GET(String.format(""));
        List<CheckIn> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockCheckIn.getClass()));  
        assertEquals(1, responseFindAll.size());
        assertEquals(testMemberId, responseFindAll.get(0).getTeamMemberId());
        assertEquals(testPdlId, responseFindAll.get(0).getPdlId());
    }

    // test Find By TeamMemberId
    @Test
    public void testGetFindByTeamMemberId() {

        if(!isDataSetupForGetTest){
            setupTestData();
            isDataSetupForGetTest = true;
        }

        HttpRequest requestFindByTeamMemberId = HttpRequest.GET(String.format("/?teamMemberId=%s", testMemberId));
        List<CheckIn> responseFindByName = client.toBlocking().retrieve(requestFindByTeamMemberId, Argument.of(List.class, mockCheckIn.getClass()));  
        assertEquals(1, responseFindByName.size());
        assertEquals(testMemberId, responseFindByName.get(0).getTeamMemberId());
        assertEquals(testPdlId, responseFindByName.get(0).getPdlId());
    }

    // test Find By TargetYearAndTargetQtr
    @Test
    public void testGetFindByTargetYearAndTargetQtr() {

        if(!isDataSetupForGetTest){
            setupTestData();
            isDataSetupForGetTest = true;
        }

        HttpRequest requestFindByTargetYearAndTargetQtr = HttpRequest.GET(String.format("/?targetYear=%s&targetQtr=%s", testYear, testQuarter));
        List<CheckIn> responseFindByTargetYearAndTargetQtr = client.toBlocking().retrieve(requestFindByTargetYearAndTargetQtr, Argument.of(List.class, mockCheckIn.getClass()));  
        assertEquals(1, responseFindByTargetYearAndTargetQtr.size());
        assertEquals(testMemberId, responseFindByTargetYearAndTargetQtr.get(0).getTeamMemberId());
        assertEquals(testYear, responseFindByTargetYearAndTargetQtr.get(0).getTargetYear());
        assertEquals(testQuarter, responseFindByTargetYearAndTargetQtr.get(0).getTargetQtr());
    }

    // test Find By PdlId
    @Test
    public void testGetFindByPdlId() {

        if(!isDataSetupForGetTest){
            setupTestData();
            isDataSetupForGetTest = true;
        }

        HttpRequest requestFindByPdlId = HttpRequest.GET(String.format("/?pdlId=%s", testPdlId));
        List<CheckIn> responseFindByPdlId = client.toBlocking().retrieve(requestFindByPdlId, Argument.of(List.class, mockCheckIn.getClass()));  
        assertEquals(1, responseFindByPdlId.size());
        assertEquals(testMemberId, responseFindByPdlId.get(0).getTeamMemberId());
        assertEquals(testPdlId, responseFindByPdlId.get(0).getPdlId());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        CheckIn testCheckIn = new CheckIn(UUID.randomUUID(), UUID.randomUUID(), testDate, "Q1", "2021");

        when(mockCheckInRepository.save(testCheckIn)).thenReturn(testCheckIn);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        UUID testPutId = UUID.randomUUID();
        UUID testPutMemberId = UUID.randomUUID();
        UUID testPutPdlId = UUID.randomUUID();
        CheckIn testCheckIn = new CheckIn(testPutMemberId, testPutPdlId, testDate, "Q1", "2025");
        testCheckIn.setId(testPutId);

        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
            put("id", testPutId);
            put("teamMemberId", testMemberId);
            put("pdlId", testPdlId);
            put("checkInDate", testDate);
            put("targetQtr", "Q1");
            put("targetYear", "2022");
        }};

        HttpRequest requestForPut = HttpRequest.PUT("", fakeBody);
        List<CheckIn> responseForPut = client.toBlocking().retrieve(requestForPut, Argument.of(List.class, mockCheckIn.getClass()));  
        assertEquals(1, responseForPut.size());
        assertEquals(testPutId, responseForPut.get(0).getId());
        assertEquals("2022", responseForPut.get(0).getTargetYear());
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

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private void setupTestData() {
        client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
    }

}