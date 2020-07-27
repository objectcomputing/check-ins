package com.objectcomputing.checkins.services.pulseResponse;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileController;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponse;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest
public class PulseResponseControllerTest {
    
    @Inject
    @Client("/pulse-response")
    private HttpClient client;

    @Inject
    MemberProfileController memberProfileController;
    
    PulseResponseRepository mockPulseResponseRepository = mock(PulseResponseRepository.class);
    
    private static LocalDate testsubmissionDate = LocalDate.of(2020, 1, 01);
    private static LocalDate testUpdatedDate = LocalDate.of(2020, 2, 01);
    private static UUID testTeamMemberId;
    private static boolean isDataSetupForTest = false;
    
    @Before
    void setup() {
        reset(mockPulseResponseRepository);
    }
    
    @BeforeAll
    void setupMemberProfileRecord() {
        // setup a record in Member-Profile to satisfy foreign key constraint

        if(memberProfileController != null) {
            MemberProfile testMemberProfile = new MemberProfile("TestName", 
                                                                "TestRole", 
                                                                UUID.randomUUID(), 
                                                                "TestLocation", 
                                                                "TestEmail", 
                                                                "TestInsperityId", 
                                                                LocalDate.of(2019, 1, 01), 
                                                                "TestBio");

            final HttpResponse<?> response = memberProfileController.save(testMemberProfile);
            assertEquals(HttpStatus.CREATED, response.getStatus());
            assertNotNull(response.body());
            testTeamMemberId = ((MemberProfile) response.body()).getUuid();
        }
    }

    @Test
    public void testFindNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By TeamMemberId returns empty array - when no data exists
    @Test
    public void testGetFindByTeamMemberIdReturnsEmptyBody() {

        UUID testUser = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse();
        List<PulseResponse> result = new ArrayList<PulseResponse>();
        result.add(pulseResponse);

        when(mockPulseResponseRepository.findByTeamMemberId(testUser)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?teamMemberId=%s", testUser)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By findBySubmissionDateBetween returns empty array - when no data exists
    @Test
    public void testGetFindByfindBySubmissionDateBetweenReturnsEmptyBody() {

        LocalDate testDateFrom = LocalDate.of(2019, 1, 01);
        LocalDate testDateTo = LocalDate.of(2019, 2, 01);
        PulseResponse pulseResponse = new PulseResponse();
        List<PulseResponse> result = new ArrayList<PulseResponse>();
        result.add(pulseResponse);

        when(mockPulseResponseRepository.findBySubmissionDateBetween(testDateFrom, testDateTo)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By TeamMemberId
    @Test
    public void testGetFindByTeamMemberId() {

        setupTestData();

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?teamMemberId=%s", testTeamMemberId)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotEquals(2, response.getContentLength());
    }
 
    // Find By findBySubmissionDateBetween
    @Test
    public void testGetFindByfindBySubmissionDateBetween() {

        setupTestData();

        LocalDate testDateFrom = LocalDate.of(2019, 1, 01);
        LocalDate testDateTo = LocalDate.of(2021, 1, 01);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)));
        
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotEquals(2, response.getContentLength());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        final Map<String, Object> testBody = new HashMap<String, Object>() {{
            put("submissionDate", testsubmissionDate);
            put("updatedDate", testUpdatedDate);
            put("teamMemberId", testTeamMemberId);
            put("internalFeelings", "operation");
            put("externalFeelings", "successful");
        }};

        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(HttpRequest.POST("", testBody));
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        // Insert value for PulseResponse
        final Map<String, Object> testBody = new HashMap<String, Object>() {{
            put("submissionDate", testsubmissionDate);
            put("updatedDate", testUpdatedDate);
            put("teamMemberId", testTeamMemberId);
            put("internalFeelings", "operation");
            put("externalFeelings", "successful");
        }};

        final HttpResponse<PulseResponse> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", testBody), PulseResponse.class);
        assertEquals(HttpStatus.CREATED, responseFromPost.getStatus());
        assertNotNull(responseFromPost.body());
        UUID testId = responseFromPost.body().getId();

        // Update value of PulseResponse
        PulseResponse testPulseResponse = new PulseResponse(testsubmissionDate, testUpdatedDate, testTeamMemberId, "Internal Feeling", "External Feeling");
        testPulseResponse.setId(testId);

        final HttpResponse<PulseResponse> responseFromPut = client.toBlocking().exchange(HttpRequest.PUT("", testPulseResponse), PulseResponse.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatus());
        assertNotNull(responseFromPut.body());
        assertEquals(testId, responseFromPut.body().getId());
        assertEquals("Internal Feeling", responseFromPut.body().getInternalFeelings());
        assertEquals("External Feeling", responseFromPut.body().getExternalFeelings());
    }

    // PUT - Request with non-existent id
    @Test
    public void testPutUpdateForNonExistentID() {

        UUID testId = UUID.randomUUID();
        final Map<String, Object> testBody = new HashMap<String, Object>() {{
            put("id", testId);
            put("submissionDate", testsubmissionDate);
            put("updatedDate", testUpdatedDate);
            put("teamMemberId", testTeamMemberId);
            put("internalFeelings", "operation");
            put("externalFeelings", "successful");
        }};

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testBody));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        PulseResponse testPulseResponse = new PulseResponse();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testPulseResponse));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        setupTestData();

        final Map<String, Object> testBody = new HashMap<String, Object>() {{
            put("submissionDate", testsubmissionDate);
            put("updatedDate", testUpdatedDate);
            put("teamMemberId", testTeamMemberId);
            put("internalFeelings", "Operation");
            put("externalFeelings", "Fails");
        }};
        
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testBody));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private void setupTestData() {

        if(!isDataSetupForTest) {

            final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
                put("submissionDate", testsubmissionDate);
                put("updatedDate", testUpdatedDate);
                put("teamMemberId", testTeamMemberId);
                put("internalFeelings", "Test Value for Internal Feeling");
                put("externalFeelings", "Test Value for External Feeling");
            }};
            
            client.toBlocking().exchange(HttpRequest.POST("", fakeBody));

            isDataSetupForTest = true;
        }
    }
}