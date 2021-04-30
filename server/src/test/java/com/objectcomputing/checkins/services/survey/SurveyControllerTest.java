package com.objectcomputing.checkins.services.survey;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SurveyFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.survey.Survey;
import com.objectcomputing.checkins.services.survey.SurveyCreateDTO;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SurveyControllerTest extends TestContainersSuite implements MemberProfileFixture, SurveyFixture {

    @Inject
    @Client("/services/survey")
    private HttpClient client;

    @Test
    public void testCreateASurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setCreatedOn(LocalDate.now());
//        surveyResponseCreateDTO.setUpdatedDate(LocalDate.now());
        surveyResponseCreateDTO.setCreatedBy(memberProfile.getId());
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Survey> response = client.toBlocking().exchange(request,Survey.class);

        Survey surveyResponseResponse = response.body();

        assertNotNull(surveyResponseResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(surveyResponseCreateDTO.getCreatedBy(),surveyResponseResponse.getCreatedBy());
        assertEquals(String.format("%s/%s", request.getPath(), surveyResponseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidSurvey() {
        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText(),
                errors.get(2).get("message").asText(), errors.get(3).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals(4,errorList.size());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateSurveyForNonExistingMember(){
        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setCreatedOn(LocalDate.now());
//        surveyResponseCreateDTO.setUpdatedDate(LocalDate.now());
        surveyResponseCreateDTO.setCreatedBy(UUID.randomUUID());
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");

        HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s doesn't exists",surveyResponseCreateDTO.getCreatedBy()),error);
    }

    @Test
    void testCreateANullSurvey() {
        final HttpRequest<String> request = HttpRequest.POST("","").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [surveyResponse] not specified",errors.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testCreateASurveyForInvalidDate() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setCreatedOn(LocalDate.of(1965,11,12));
//        surveyResponseCreateDTO.setUpdatedDate(LocalDate.of(1965,11,12));
        surveyResponseCreateDTO.setCreatedBy(memberProfile.getId());
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Invalid date for survey submission date %s",surveyResponseCreateDTO.getCreatedBy()),error);

    }

    @Test
    public void testGetFindByCreatedBy() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", surveyResponse.getCreatedBy())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));
        assertEquals(Set.of(surveyResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    // Find By findByCreatedOnBetween returns empty array - when no data exists
    @Test
    public void testGetFindByCreatedOnBetweenReturnsEmptyBody() {

        LocalDate testDateFrom = LocalDate.of(2019, 01, 01);
        LocalDate testDateTo = LocalDate.of(2019, 02, 01);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By findByCreatedOnBetween
    @Test
    public void testGetFindByfindByCreatedOnBetween() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        LocalDate testDateFrom = LocalDate.of(2019, 01, 01);
        LocalDate testDateTo = Util.MAX.toLocalDate();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?dateFrom=%tF&dateTo=%tF", testDateFrom, testDateTo)).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotEquals(2, response.getContentLength());
    }

    @Test
    public void testGetFindById() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", surveyResponse.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));
        assertEquals(Set.of(surveyResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testFindSurveyAllParams(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", surveyResponse.getCreatedBy())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertEquals(Set.of(surveyResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testSurveyDoesNotExist() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s",UUID.randomUUID())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testUpdateSurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Survey> response = client.toBlocking().exchange(request, Survey.class);

        assertEquals(surveyResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), surveyResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateNonExistingSurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setId(UUID.randomUUID());

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find survey record with id %s", surveyResponse.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingMemberSurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setCreatedBy(UUID.randomUUID());

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", surveyResponse.getCreatedBy()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotMemberSurveyWithoutId(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setId(null);

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find survey record with id null", surveyResponse.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateUnAuthorized() {
        Survey surveyResponse = new Survey(LocalDate.now(),UUID.randomUUID(),"internalfeeling","externalfeeling");

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());

    }

    @Test
    void testUpdateANullSurvey() {
        final HttpRequest<String> request = HttpRequest.PUT("","").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [surveyResponse] not specified",errors.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testUpdateInvalidDateSurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setCreatedOn(LocalDate.of(1965,12,11));

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for survey submission date %s", surveyResponse.getCreatedBy()), error);
        assertEquals(request.getPath(), href);

    }
}
