package com.objectcomputing.checkins.services.memberprofile;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.Resource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture,
    SkillFixture, MemberSkillFixture, TeamFixture, TeamMemberFixture, RoleFixture {

    @Inject
    @Client("/services/member-profiles")
    private HttpClient client;

    private final LocalDate maxDate = Util.MAX.toLocalDate();

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private MemberProfile member;
    private MemberProfile pdl;
    private MemberProfile admin;

    @BeforeEach
    void makeRoles() {
        createAndAssignRoles();
        pdl = createADefaultMemberProfile();
        member = createADefaultMemberProfileForPdl(pdl);
        admin = createASecondDefaultMemberProfile();

        assignAdminRole(admin);
        assignPdlRole(pdl);
    }

    @Test
    void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET(String.valueOf(UUID.randomUUID()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testDeleteThrowsExceptionWhenUserDoesNotExist() {

        final HttpRequest<?> request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

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
    void testDeleteThrowsExceptionIfCheckinDataExists() {

        createADefaultCheckIn(member, pdl);

        final HttpRequest<?> request = HttpRequest.DELETE(member.getId().toString())
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since Checkin record(s) exist", MemberProfileUtils.getFullName(member)),
            responseException.getMessage()
        );
    }

    @Test
    void testDeleteThrowsExceptionIfMemberSkillExists() {

        Skill testSkill = createADefaultSkill();
        createMemberSkill(member, testSkill, "Pro", LocalDate.now());

        final HttpRequest<?> request = HttpRequest.DELETE(member.getId().toString())
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since MemberSkill record(s) exist", MemberProfileUtils.getFullName(member)),
            responseException.getMessage()
        );
    }

    @Test
    void testDeleteThrowsExceptionIfMemberIsPartOfATeam() {

        Team testTeam = createDefaultTeam();
        createDefaultTeamMember(testTeam, member);

        final HttpRequest<?> request = HttpRequest.DELETE(member.getId().toString())
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since TeamMember record(s) exist", MemberProfileUtils.getFullName(member)),
            responseException.getMessage()
        );
    }

    @Test
    void testDeleteThrowsExceptionIfMemberHasPDLRole() {

        final HttpRequest<?> request = HttpRequest.DELETE(pdl.getId().toString())
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since user has PDL role", MemberProfileUtils.getFullName(pdl)),
            responseException.getMessage()
        );
    }

    @Test
    void testDeleteHappyPath() {

        final HttpRequest<?> request = HttpRequest.DELETE(member.getId().toString())
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is Deleted and not Terminated
        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", member.getId()))
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No member profile for id " + member.getId(), error);
    }

    @Test
    void testDeleteNotAuthorized() {
        final HttpRequest<?> request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

    @Test
    void testDeleteNoPermission() {
        MemberProfile pdlMember = createAnUnrelatedUser();
        assignPdlRole(pdlMember);

        final HttpRequest<?> request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
            .basicAuth(pdlMember.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

    // Find By id - when no user data exists
    @Test
    void testGETFindByNameReturnsEmptyBody() throws UnsupportedEncodingException {

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETFindByValueName() throws UnsupportedEncodingException {

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/?firstName=%s", encodeValue(member.getFirstName()))).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(member), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETGetByIdHappyPath() {

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/%s", member.getId())).basicAuth(member.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertEquals(member, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
            GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindByMemberName() throws UnsupportedEncodingException {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?firstName=%s", encodeValue(member.getFirstName())))
            .basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(member), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindByMemberRole() throws UnsupportedEncodingException {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?title=%s", encodeValue(member.getTitle()))).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(member), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindByMemberPdlId() {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", member.getPdlId())).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(member), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindBySupervisorId() {
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?supervisorId=%s", member.getSupervisorid()))
            .basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(member), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testPOSTCreateAMemberProfileAdmin() {

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
            POST("/", dto).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertNotNull(response);
        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(MemberProfileUtils.getFullName(dto), MemberProfileUtils.getFullName(response.body()));
        assertEquals(String.format("%s/%s", request.getPath(), response.getBody().get().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    void testPOSTCreateAMemberProfileMemberUnauthorized() {

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
            POST("/", dto).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testPOSTCreateAMemberProfilePdlUnauthorized() {

        MemberProfile pdl = createAThirdDefaultMemberProfile();
        assignPdlRole(pdl);

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
            POST("/", dto).basicAuth(pdl.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    public void assertUnauthorized(HttpClientResponseException exception) {
        assertEquals("Forbidden", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testPOSTCreateANullMemberProfile() {
        MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
            POST("/", memberProfileCreateDTO).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    // Find By id - when no user data exists for POST
    @Test
    void testPostValidationFailures() {
        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.POST("", new MemberProfileCreateDTO())
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(6, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testPOSTSaveMemberSameEmailThrowsException() {
        // create a user
        MemberProfileUpdateDTO firstUser = mkUpdateMemberProfileDTO();

        final HttpRequest<?> requestFirstUser = HttpRequest.POST("/", firstUser).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<MemberProfile> responseFirstUser = client.toBlocking().exchange(requestFirstUser, MemberProfile.class);

        assertNotNull(responseFirstUser);
        assertEquals(HttpStatus.CREATED, responseFirstUser.getStatus());

        // create another user with same email address
        MemberProfileUpdateDTO secondUser = mkUpdateMemberProfileDTO();
        final HttpRequest<?> requestSecondUser = HttpRequest.POST("/", secondUser).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);

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
    void testPUTUpdateMemberProfile() {

        // Create a DTO and them update it with the "member" information.
        MemberProfileUpdateDTO profileUpdateDTO = mkUpdateMemberProfileDTO();
        profileUpdateDTO.setId(member.getId());
        profileUpdateDTO.setWorkEmail(member.getWorkEmail());

        final HttpRequest<MemberProfileUpdateDTO> request = HttpRequest.PUT("/", profileUpdateDTO)
            .basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<MemberProfileResponseDTO> response = client.toBlocking().exchange(request, MemberProfileResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        assertProfilesEqual(profileUpdateDTO, response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), profileUpdateDTO.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    void testPUTUpdateNonexistentMemberProfile() {

        MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();
        memberProfileCreateDTO.setFirstName("reincarnation");
        memberProfileCreateDTO.setLastName("gentleman");

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
            PUT("/", memberProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testPUTUpdateNullMemberProfile() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    // POST - Future Start Date
    @Test
    void testPostForFutureStartDate() {
        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
        requestBody.setStartDate(maxDate);

        final HttpResponse<MemberProfileResponseDTO> response = client
            .toBlocking()
            .exchange(HttpRequest.POST("", requestBody)
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE), MemberProfileResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        var body = response.getBody().get();
        assertProfilesEqual(requestBody, body);
        assertEquals("/member-profiles/" + body.getId(), response.header("location"));
    }

    // POST - NotBlank MemberProfile first name (and last name)
    @Test
    void testPostWithNullName() {
        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
        requestBody.setFirstName(null);

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.POST("", requestBody)
            .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Find By id - when no user data exists for PUT
    @Test
    void testPutValidationFailures() {

        final HttpRequest<MemberProfileUpdateDTO> request = HttpRequest.PUT("", new MemberProfileUpdateDTO())
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(4, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Future Start Date
    @Test
    void testPutFutureStartDate() {

        // Create a DTO and them update it with the "member" information.
        MemberProfileUpdateDTO requestBody = mkUpdateMemberProfileDTO();
        requestBody.setId(member.getId());
        requestBody.setWorkEmail(member.getWorkEmail());
        requestBody.setStartDate(maxDate);

        final HttpResponse<MemberProfileResponseDTO> response = client
            .toBlocking()
            .exchange(HttpRequest.PUT("", requestBody)
                        .basicAuth(member.getWorkEmail(), MEMBER_ROLE),
                      MemberProfileResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertProfilesEqual(requestBody, Objects.requireNonNull(response.body()));
    }

    // PUT - Request with empty body
    @Test
    void testPutUpdateForEmptyInput() {
        MemberProfileUpdateDTO testMemberProfile = mkUpdateMemberProfileDTO();
        testMemberProfile.setId(null);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testMemberProfile)
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        assertEquals("memberProfile.id: must not be null", error.asText());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testMemberProfileWithNullTerminationDate() {
        final HttpResponse<List < MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("").basicAuth(member.getWorkEmail(), MEMBER_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        var body = response.getBody().get();
        assertEquals(3, body.size());
    }

    @Test
    void testMemberProfileWithUpcomingTerminationDate() {
        MemberProfile memberProfile = createAFutureTerminatedMemberProfile();

        final HttpResponse<List < MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        var body = response.getBody().get();
        assertEquals(4, body.size());
    }

    @Test
    void testMemberProfileWithPreviousTerminationDate() {
        MemberProfile memberProfile = createAPastTerminatedMemberProfile();

        final HttpResponse<List <MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        var body = response.getBody().get();
        // There are 4 total members, but we should only see 3 since one was
        // terminated prior to today.
        assertEquals(3, body.size());
    }

    @Test
    void testMemberProfileWithPreviousTerminationDateReturnsWhenTerminatedTrue() {
        MemberProfile memberProfile = createAPastTerminatedMemberProfile();

        final HttpResponse<List <MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("/?terminated=true").basicAuth(ADMIN_ROLE, ADMIN_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        var body = response.getBody().get();
        assertEquals(1, body.size());
        assertProfilesEqual(memberProfile, Objects.requireNonNull(body.get(0)));
    }
}
