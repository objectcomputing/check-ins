package com.objectcomputing.checkins.services.opportunities;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OpportunitiesFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpportunitiesControllerTest extends TestContainersSuite implements MemberProfileFixture, OpportunitiesFixture, RoleFixture {

    @Inject
    @Client("/services/opportunities")
    private HttpClient client;

    @Test
    public void testCreateAOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        OpportunitiesCreateDTO opportunitiesResponseCreateDTO = new OpportunitiesCreateDTO();
        opportunitiesResponseCreateDTO.setName("Name");
        opportunitiesResponseCreateDTO.setDescription("Description");
        opportunitiesResponseCreateDTO.setUrl("https://objectcomputing.com/jobs");
        opportunitiesResponseCreateDTO.setExpiresOn(LocalDate.now());
        opportunitiesResponseCreateDTO.setPending(Boolean.FALSE);

        final HttpRequest<OpportunitiesCreateDTO> request = HttpRequest.POST("",opportunitiesResponseCreateDTO).basicAuth(memberProfile.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<Opportunities> response = client.toBlocking().exchange(request,Opportunities.class);

        Opportunities opportunitiesResponseResponse = response.body();

        assertNotNull(opportunitiesResponseResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), opportunitiesResponseResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testMemberCreateAnOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        OpportunitiesCreateDTO opportunitiesResponseCreateDTO = new OpportunitiesCreateDTO();
        opportunitiesResponseCreateDTO.setName("Name");
        opportunitiesResponseCreateDTO.setDescription("Description");
        opportunitiesResponseCreateDTO.setUrl("https://objectcomputing.com/jobs");
        opportunitiesResponseCreateDTO.setExpiresOn(LocalDate.now());
        opportunitiesResponseCreateDTO.setPending(Boolean.FALSE);

        final HttpRequest<OpportunitiesCreateDTO> request = HttpRequest.POST("",opportunitiesResponseCreateDTO).basicAuth(memberProfile.getWorkEmail(),MEMBER_ROLE);
        final HttpResponse<Opportunities> response = client.toBlocking().exchange(request, Opportunities.class);

        Opportunities opportunitiesResponseResponse = response.body();

        assertNotNull(opportunitiesResponseResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), opportunitiesResponseResponse.getId()), response.getHeaders().get("location"));
    }
    @Test
    void testCreateAnInvalidOpportunities() {
        OpportunitiesCreateDTO opportunitiesResponseCreateDTO = new OpportunitiesCreateDTO();

        final HttpRequest<OpportunitiesCreateDTO> request = HttpRequest.POST("",opportunitiesResponseCreateDTO).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
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
    void testCreateANullOpportunities() {
        final HttpRequest<String> request = HttpRequest.POST("","").basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [opportunitiesResponse] not specified",error.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    public void testGETFindByValueName() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", opportunitiesResponse.getName())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        final HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));
        assertEquals(Set.of(opportunitiesResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGetFindBySubmittedBy() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?submittedBy=%s", opportunitiesResponse.getSubmittedBy())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        final HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));
        assertEquals(Set.of(opportunitiesResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGetFindByDescription() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?description=%s", opportunitiesResponse.getDescription())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        final HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));
        assertEquals(Set.of(opportunitiesResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }


    @Test
    public void testGetFindByPending() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pending=%s", opportunitiesResponse.getPending())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        final HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));
        assertEquals(Set.of(opportunitiesResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGetFindAll() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<Object> request = HttpRequest.GET("/").basicAuth(ADMIN_ROLE,ADMIN_ROLE);

        final HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
        response.equals(opportunitiesResponse);
    }

    @Test
    void testFindOpportunitiesAllParams(){
        // TODO matt time issue here as well
        MemberProfile memberProfile = createADefaultMemberProfile();

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?submittedBy=%s", opportunitiesResponse.getSubmittedBy())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        final HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));

        assertEquals(Set.of(opportunitiesResponse), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testOpportunitiesDoesNotExist() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?submittedBy=%s",UUID.randomUUID())).basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        HttpResponse<Set<Opportunities>> response = client.toBlocking().exchange(request, Argument.setOf(Opportunities.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testUpdateOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse)
                .basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Opportunities> response = client.toBlocking().exchange(request, Opportunities.class);

        assertEquals(opportunitiesResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), opportunitiesResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateNonExistingOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);
        opportunitiesResponse.setId(UUID.randomUUID());

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse)
                .basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find opportunities record with id %s", opportunitiesResponse.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingMemberOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);
        opportunitiesResponse.setSubmittedBy(UUID.randomUUID());

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse)
                .basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", opportunitiesResponse.getSubmittedBy()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotMemberOpportunitiesWithoutId(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);
        opportunitiesResponse.setId(null);

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse)
                .basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find opportunities record with id null", opportunitiesResponse.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateUnAuthorized() {
        Opportunities opportunitiesResponse = new Opportunities("jobOpportunities","opportunities job interests","https://objectcomputing.com/jobs", LocalDate.now(), LocalDate.now(), UUID.randomUUID(), Boolean.TRUE );

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());

    }

    @Test
    void testUpdateANullOpportunities() {
        final HttpRequest<String> request = HttpRequest.PUT("","").basicAuth(ADMIN_ROLE,ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [opportunitiesResponse] not specified",error.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testUpdateInvalidDateOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);
        opportunitiesResponse.setSubmittedOn(LocalDate.of(1965,12,11));

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse)
                .basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for opportunities submission date %s", opportunitiesResponse.getSubmittedBy()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    public void testMemberGETFindByValueName() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", opportunitiesResponse.getName())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Opportunities> response = client.toBlocking().exchange(request, Opportunities.class);

        assertEquals(opportunitiesResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    public void testMemberGetFindBySubmittedBy() {

        MemberProfile memberProfile = createADefaultMemberProfile();


        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?submittedBy=%s", opportunitiesResponse.getSubmittedBy())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Opportunities> response = client.toBlocking().exchange(request, Opportunities.class);

        assertEquals(opportunitiesResponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testMemberUpdateOpportunities(){
        MemberProfile memberProfile = createADefaultMemberProfile();

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<Opportunities> request = HttpRequest.PUT("", opportunitiesResponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }

    @Test
    void testdeleteOpportunitiesByTeamByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", opportunitiesResponse.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }

    @Test
    void testdeleteOpportunitiesIfAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createAndAssignAdminRole(memberProfile);

        Opportunities opportunitiesResponse  = createADefaultOpportunities(memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", opportunitiesResponse.getId())).basicAuth(memberProfile.getWorkEmail(),ADMIN_ROLE);

        final HttpResponse<Boolean> response = client.toBlocking().exchange(request, Boolean.class);

        assertEquals(HttpStatus.OK,response.getStatus());

    }

}
