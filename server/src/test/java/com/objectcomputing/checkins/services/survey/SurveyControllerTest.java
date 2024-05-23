package com.objectcomputing.checkins.services.survey;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SurveyFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurveyControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, SurveyFixture {

    @Inject
    @Client("/services/surveys")
    private HttpClient client;

    @Test
    void testCreateASurvey(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");
        surveyResponseCreateDTO.setCreatedOn(LocalDate.now());
        surveyResponseCreateDTO.setCreatedBy(memberProfile.getId());

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
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

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
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
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");
        surveyResponseCreateDTO.setCreatedOn(LocalDate.now());
        surveyResponseCreateDTO.setCreatedBy(UUID.randomUUID());

        HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
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
        final HttpRequest<String> request = HttpRequest.POST("","").basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [surveyResponse] not specified",error.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testCreateASurveyForInvalidDate() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();
        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");
        surveyResponseCreateDTO.setCreatedOn(LocalDate.of(1965,11,12));
        surveyResponseCreateDTO.setCreatedBy(memberProfile.getId());

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Invalid date for survey submission date %s",surveyResponseCreateDTO.getCreatedBy()),error);

    }

    @Test
    void testGETFindByValueName() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", surveyResponse.getName())).basicAuth(user.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));
        assertEquals(Set.of(surveyResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testGetFindByCreatedBy() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", surveyResponse.getCreatedBy())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));
        assertEquals(Set.of(surveyResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testGetFindAll() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<Object> request = HttpRequest.GET("/").basicAuth(user.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(Objects.requireNonNull(response.body()).contains(surveyResponse));
    }

    @Test
    void testFindSurveyByCreateBy(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey expectedResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", expectedResponse.getCreatedBy())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));


        assertTrue(Objects.requireNonNull(response.body()).contains(expectedResponse));
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    void testFindSurveyAllParams(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey expectedResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&createdBy=%s", expectedResponse.getName(), expectedResponse.getCreatedBy())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertTrue(Objects.requireNonNull(response.body()).contains(expectedResponse));
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    void testFindSurveyAllParams_WrongCreateByID(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey expectedResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&createdBy=%s", expectedResponse.getName(), UUID.randomUUID())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));


        assertFalse(Objects.requireNonNull(response.body()).contains(expectedResponse));
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    void testSurveyDoesNotExist() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s",UUID.randomUUID())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testUpdateSurvey(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Survey> response = client.toBlocking().exchange(request, Survey.class);

        assertEquals(surveyResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), surveyResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateNonExistingSurvey(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setId(UUID.randomUUID());

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
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
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setCreatedBy(UUID.randomUUID());

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
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
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setId(null);

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
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
        Survey surveyResponse = new Survey(LocalDate.now(),UUID.randomUUID(),"jobSurvey","survey job interests");

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());

    }

    @Test
    void testUpdateANullSurvey() {
        final HttpRequest<String> request = HttpRequest.PUT("","").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [surveyResponse] not specified",error.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testUpdateInvalidDateSurvey(){
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        surveyResponse.setCreatedOn(LocalDate.of(1965,12,11));

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for survey submission date %s", surveyResponse.getCreatedBy()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testMemberCreateASurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        SurveyCreateDTO surveyResponseCreateDTO = new SurveyCreateDTO();
        surveyResponseCreateDTO.setName("Name");
        surveyResponseCreateDTO.setDescription("Description");
        surveyResponseCreateDTO.setCreatedOn(LocalDate.now());
        surveyResponseCreateDTO.setCreatedBy(memberProfile.getId());

        final HttpRequest<SurveyCreateDTO> request = HttpRequest.POST("",surveyResponseCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }

    @Test
    void testMemberGETFindByValueName() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", surveyResponse.getName())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testMemberGetFindByCreatedBy() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", surveyResponse.getCreatedBy())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testMemberUpdateSurvey(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<Survey> request = HttpRequest.PUT("", surveyResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }

    @Test
    void deleteTeamByMember() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        Survey surveyResponse  = createADefaultSurvey(memberProfile);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", surveyResponse.getId())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<Survey>> response = client.toBlocking().exchange(request, Argument.setOf(Survey.class));

        assertEquals(HttpStatus.OK,response.getStatus());
    }

}
