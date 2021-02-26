package com.objectcomputing.checkins.services.memberprofile;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemberProfileControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture,
        SkillFixture, MemberSkillFixture, TeamFixture, TeamMemberFixture, RoleFixture {

    @Inject
    @Client("/services/member-profile")
    private HttpClient client;

    /*
     * LocalDate.Max cannot be used for end-to-end tests
     * LocalDate.Max year = 999999999
     * POSTGRES supported date range = 4713 BC - 5874897 AD
     */
    private final LocalDate maxDate = LocalDate.of(2099, 12, 31);

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET(String.valueOf(UUID.randomUUID()))
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testDeleteThrowsExceptionWhenUserDoesNotExist() {

        final HttpRequest request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No member profile for id", error);
    }

    @Test
    public void testDeleteTerminatesUserIfCheckinDataExists() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        createADefaultCheckIn(memberProfileOfMember, memberProfileOfPDL);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is terminated and not deleted
        final HttpRequest<Object> requestForAssertingTermination = HttpRequest.
                GET(String.format("/%s", memberProfileOfMember.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<MemberProfile> responseForAssertingTermination = client.toBlocking().exchange(requestForAssertingTermination, MemberProfile.class);

        assertEquals(HttpStatus.OK, responseForAssertingTermination.getStatus());
        assertNotNull(responseForAssertingTermination.body());
        MemberProfile result = responseForAssertingTermination.body();
        assertNotNull(result);
        assertNotNull(result.getTerminationDate());
        assertEquals(LocalDate.now(), result.getTerminationDate());
        Assertions.assertNull(result.getPdlId());
    }

    @Test
    public void testDeleteTerminatesUserIfMemberSkillExists() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        Skill testSkill = createADefaultSkill();
        createMemberSkill(memberProfileOfMember, testSkill, "Pro", LocalDate.now());

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is terminated and not deleted
        final HttpRequest<Object> requestForAssertingTermination = HttpRequest.
                GET(String.format("/%s", memberProfileOfMember.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<MemberProfile> responseForAssertingTermination = client.toBlocking().exchange(requestForAssertingTermination, MemberProfile.class);

        assertEquals(HttpStatus.OK, responseForAssertingTermination.getStatus());
        assertNotNull(responseForAssertingTermination.body());
        MemberProfile result = responseForAssertingTermination.body();
        assertNotNull(result);
        assertNotNull(result.getTerminationDate());
        assertEquals(LocalDate.now(), result.getTerminationDate());
        Assertions.assertNull(result.getPdlId());
    }

    @Test
    public void testDeleteTerminatesUserIfMemberIsPartOfATeam() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        Team testTeam = createDeafultTeam();
        createDeafultTeamMember(testTeam, memberProfileOfMember);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is terminated and not deleted
        final HttpRequest<Object> requestForAssertingTermination = HttpRequest.
                GET(String.format("/%s", memberProfileOfMember.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<MemberProfile> responseForAssertingTermination = client.toBlocking().exchange(requestForAssertingTermination, MemberProfile.class);

        assertEquals(HttpStatus.OK, responseForAssertingTermination.getStatus());
        assertNotNull(responseForAssertingTermination.body());
        MemberProfile result = responseForAssertingTermination.body();
        assertNotNull(result);
        assertNotNull(result.getTerminationDate());
        assertEquals(LocalDate.now(), result.getTerminationDate());
        Assertions.assertNull(result.getPdlId());
    }

    @Test
    public void testDeleteTerminatesUserIfMemberHasPDLRole() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        createDefaultRole(RoleType.PDL, memberProfileOfPDL);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfPDL.getId().toString())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is terminated and not deleted
        final HttpRequest<Object> requestForAssertingTermination = HttpRequest.
                GET(String.format("/%s", memberProfileOfPDL.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<MemberProfile> responseForAssertingTermination = client.toBlocking().exchange(requestForAssertingTermination, MemberProfile.class);

        assertEquals(HttpStatus.OK, responseForAssertingTermination.getStatus());
        assertNotNull(responseForAssertingTermination.body());
        MemberProfile result = responseForAssertingTermination.body();
        assertNotNull(result);
        assertNotNull(result.getTerminationDate());
        assertEquals(LocalDate.now(), result.getTerminationDate());
        Assertions.assertNull(result.getPdlId());

        // Ensure PDL record is updated for people under the terminated user
        final HttpRequest<Object> requestForTestingPDLRecord = HttpRequest.GET(String.format("/%s", memberProfileOfMember.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<MemberProfile> responseForTestingPDLRecord = client.toBlocking().exchange(requestForTestingPDLRecord, MemberProfile.class);

        assertEquals(HttpStatus.OK, responseForTestingPDLRecord.getStatus());
        assertNotNull(responseForTestingPDLRecord.body());
        MemberProfile memberProfileOfEmployee = responseForTestingPDLRecord.body();
        assertNotNull(memberProfileOfEmployee);
        Assertions.assertNull(result.getPdlId());
        assertNull(memberProfileOfEmployee.getTerminationDate());
    }

    @Test
    public void testDeleteHappyPath() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is Deleted and not Terminated
        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", memberProfileOfMember.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No member profile for id", error);
    }

    @Test
    public void testDeleteNotAuthorized() {

        final HttpRequest request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

    // Find By id - when no user data exists
    @Test
    public void testGETFindByNameReturnsEmptyBody() throws UnsupportedEncodingException {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByValueName() throws UnsupportedEncodingException {

        MemberProfile memberProfile = createADefaultMemberProfile();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue(memberProfile.getName()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(memberProfile), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETGetByIdHappyPath() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", memberProfile.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertEquals(memberProfile, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindByMemberName() throws UnsupportedEncodingException {
        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", encodeValue(memberProfile.getName()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(memberProfile), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindByMemberRole() throws UnsupportedEncodingException {
        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?title=%s", encodeValue(memberProfile.getTitle()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(memberProfile), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindByMemberPdlId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfilePdl = createADefaultMemberProfileForPdl(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", memberProfilePdl.getPdlId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(memberProfilePdl), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindBySupervisorId() {
        MemberProfile memberProfileOfPdl = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPdl);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?supervisorId=%s", memberProfileOfUser.getSupervisorid()))
                .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(memberProfileOfUser), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testPOSTCreateAMemberProfile() {

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPOSTCreateANullMemberProfile() {

        MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
                POST("/", memberProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    // Find By id - when no user data exists for POST
    @Test
    public void testPostValidationFailures() {

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.POST("", new MemberProfileCreateDTO())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(4, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPOSTSaveMemberSameEmailThrowsException() {
        // create a user
        MemberProfileUpdateDTO firstUser = mkUpdateMemberProfileDTO();

        final HttpRequest<?> requestFirstUser = HttpRequest.POST("/", firstUser).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberProfile> responseFirstUser = client.toBlocking().exchange(requestFirstUser, MemberProfile.class);

        assertNotNull(responseFirstUser);
        assertEquals(HttpStatus.CREATED, responseFirstUser.getStatus());

        // create another user with same email address
        MemberProfileUpdateDTO secondUser = mkUpdateMemberProfileDTO();
        final HttpRequest<?> requestSecondUser = HttpRequest.POST("/", secondUser).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestSecondUser, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(requestSecondUser.getPath(), href);
        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
        assertEquals(String.format("Email %s already exists in database", firstUser.getWorkEmail()), error);
    }

    @Test
    public void testPUTUpdateMemberProfile() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        memberProfile.setStartDate(LocalDate.of(2019, 1, 01));

        final HttpRequest<MemberProfile> request = HttpRequest.PUT("/", memberProfile)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertEquals(memberProfile, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), memberProfile.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNonexistentMemberProfile() {

        MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();
        memberProfileCreateDTO.setName("reincarnation");

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
                PUT("/", memberProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTUpdateNullMemberProfile() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    // POST - Future Start Date
    @Test
    public void testPostForFutureStartDate() {
        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
        requestBody.setStartDate(maxDate);

        final HttpResponse<MemberProfileResponseDTO> response = client
                .toBlocking()
                .exchange(HttpRequest.POST("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE), MemberProfileResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(requestBody, response.body());
        assertEquals("/member-profile/" + response.body().getId(), response.header("location"));
    }

    // POST - Nullable MemberProfile name
    @Test
    public void testPostWithNullName() {

        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
        requestBody.setName(null);

        final HttpResponse<MemberProfileResponseDTO> response = client
                .toBlocking()
                .exchange(HttpRequest.POST("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE), MemberProfileResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(requestBody, response.body());
        assertEquals("/member-profile/" + response.body().getId(), response.header("location"));
    }

    // Find By id - when no user data exists for PUT
    @Test
    public void testPutValidationFailures() {

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.PUT("", new MemberProfileCreateDTO())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(2, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Future Start Date
    @Test
    public void testPutFutureStartDate() {

        MemberProfileUpdateDTO requestBody = mkUpdateMemberProfileDTO();
        requestBody.setStartDate(maxDate);

        final HttpResponse<MemberProfileResponseDTO> response = client
                .toBlocking()
                .exchange(HttpRequest.PUT("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE), MemberProfileResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertProfilesEqual(requestBody, Objects.requireNonNull(response.body()));
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        MemberProfileUpdateDTO testMemberProfile = mkUpdateMemberProfileDTO();
        testMemberProfile.setId(null);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testMemberProfile)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
        assertEquals("memberProfile.id: must not be null", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }
}
