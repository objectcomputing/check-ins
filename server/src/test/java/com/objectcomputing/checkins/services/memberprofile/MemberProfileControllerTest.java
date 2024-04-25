package com.objectcomputing.checkins.services.memberprofile;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.assertProfilesEqual;
import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkCreateMemberProfileDTO;
import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkUpdateMemberProfileDTO;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.PDL_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.TeamMemberFixture;
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
import jakarta.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberProfileControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture,
    SkillFixture, MemberSkillFixture, TeamFixture, TeamMemberFixture, RoleFixture {

    @Inject
    @Client("/services/member-profiles")
    private HttpClient client;

    //TODO: Use Util.MAX instead of defining variable
    /*
     * LocalDate.Max cannot be used for end-to-end tests
     * LocalDate.Max year = 999999999
     * POSTGRES supported date range = 4713 BC - 5874897 AD
     */
    private final LocalDate maxDate = LocalDate.of(2099, 12, 31);

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
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

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);
//        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

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
    public void testDeleteThrowsExceptionIfCheckinDataExists() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        createADefaultCheckIn(memberProfileOfMember, memberProfileOfPDL);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);
//        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since Checkin record(s) exist", MemberProfileUtils.getFullName(memberProfileOfMember)),
            responseException.getMessage()
        );
    }

    @Test
    public void testDeleteThrowsExceptionIfMemberSkillExists() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        Skill testSkill = createADefaultSkill();
        createMemberSkill(memberProfileOfMember, testSkill, "Pro", LocalDate.now());

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);
//        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since MemberSkill record(s) exist", MemberProfileUtils.getFullName(memberProfileOfMember)),
            responseException.getMessage()
        );
    }

    @Test
    public void testDeleteThrowsExceptionIfMemberIsPartOfATeam() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        Team testTeam = createDefaultTeam();
        createDefaultTeamMember(testTeam, memberProfileOfMember);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);
//        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since TeamMember record(s) exist", MemberProfileUtils.getFullName(memberProfileOfMember)),
            responseException.getMessage()
        );
    }

    @Test
    public void testDeleteThrowsExceptionIfMemberHasPDLRole() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        assignPdlRole(memberProfileOfPDL);
//        createAndAssignRole(RoleType.PDL, memberProfileOfPDL);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);
//        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfPDL.getId().toString())
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(
            String.format("User %s cannot be deleted since user has PDL role", MemberProfileUtils.getFullName(memberProfileOfPDL)),
            responseException.getMessage()
        );
    }

    @Test
    public void testDeleteHappyPath() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);
//        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

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
        assertEquals("No member profile for id " + memberProfileOfMember.getId(), error);
    }

    @Test
    public void testDeleteNotAuthorized() {
        final HttpRequest request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

    @Test
    public void testDeleteNoPermission() {
        MemberProfile pdlMember = createAnUnrelatedUser();
        assignPdlRole(pdlMember);

        final HttpRequest request = HttpRequest.DELETE("/01b7d769-9fa2-43ff-95c7-f3b950a27bf9")
            .basicAuth(pdlMember.getWorkEmail(), PDL_ROLE);
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
            GET(String.format("/?firstName=%s", encodeValue(memberProfile.getFirstName()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

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

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?firstName=%s", encodeValue(memberProfile.getFirstName())))
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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
    public void testPOSTCreateAMemberProfileAdmin() {

        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
            POST("/", dto).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(MemberProfileUtils.getFullName(dto), MemberProfileUtils.getFullName(response.body()));
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPOSTCreateAMemberProfileMemberUnauthorized() {

        MemberProfile member = createADefaultMemberProfile();
        assignMemberRole(member);

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
            POST("/", dto).basicAuth(member.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    public void testPOSTCreateAMemberProfilePdlUnauthorized() {

        MemberProfile pdl = createADefaultMemberProfile();
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
    public void testPOSTCreateANullMemberProfile() {
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);

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
    public void testPostValidationFailures() {
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);

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
    public void testPOSTSaveMemberSameEmailThrowsException() {
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);

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
    public void testPUTUpdateMemberProfile() {

        MemberProfileUpdateDTO profileUpdateDTO = mkUpdateMemberProfileDTO();

        final HttpRequest<MemberProfileUpdateDTO> request = HttpRequest.PUT("/", profileUpdateDTO)
            .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberProfileResponseDTO> response = client.toBlocking().exchange(request, MemberProfileResponseDTO.class);

        assertProfilesEqual(profileUpdateDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), profileUpdateDTO.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNonexistentMemberProfile() {

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
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);

        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
        requestBody.setStartDate(maxDate);

        final HttpResponse<MemberProfileResponseDTO> response = client
            .toBlocking()
            .exchange(HttpRequest.POST("", requestBody)
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE), MemberProfileResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(requestBody, response.body());
        assertEquals("/member-profiles/" + response.body().getId(), response.header("location"));
    }

    // POST - NotBlank MemberProfile first name (and last name)
    @Test
    public void testPostWithNullName() {
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);

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
    public void testPutValidationFailures() {

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
        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        assertEquals("memberProfile.id: must not be null", error.asText());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testMemberProfileWithNullTerminationDate() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        final HttpResponse<List < MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.body().size());
        assertProfilesEqual(memberProfile, Objects.requireNonNull(response.body().get(0)));
    }

    @Test
    public void testMemberProfileWithUpcomingTerminationDate() {
        MemberProfile memberProfile = createAFutureTerminatedMemberProfile();

        final HttpResponse<List < MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.body().size());
        assertProfilesEqual(memberProfile, Objects.requireNonNull(response.body().get(0)));
    }

    @Test
    public void testMemberProfileWithPreviousTerminationDate() {
        MemberProfile memberProfile = createAPastTerminatedMemberProfile();

        final HttpResponse<List <MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0, response.body().size());
    }

    @Test
    public void testMemberProfileWithPreviousTerminationDateReturnsWhenTerminatedTrue() {
        MemberProfile memberProfile = createAPastTerminatedMemberProfile();

        final HttpResponse<List <MemberProfileResponseDTO >> response = client
            .toBlocking()
            .exchange(HttpRequest.GET("/?terminated=true").basicAuth(ADMIN_ROLE, ADMIN_ROLE), Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.body().size());
        assertProfilesEqual(memberProfile, Objects.requireNonNull(response.body().get(0)));
    }

}
