package com.objectcomputing.checkins.services.demographics;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.DemographicsFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.test.StepVerifier;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class DemographicsControllerTest extends TestContainersSuite implements DemographicsFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/demographics")
    private HttpClient client;

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Test
    public void testGetAllDemographicsUnauthorized() {

        final HttpRequest<Object> request = HttpRequest.
                GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    public void testPostUnauthorized() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        DemographicsCreateDTO newDemographics = new DemographicsCreateDTO();
        newDemographics.setMemberId(alice.getId());
        newDemographics.setGender("female");

        final HttpRequest<DemographicsCreateDTO> request = HttpRequest.
                POST("/", newDemographics);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    public void testGetAllDemographics() {

        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        createAndAssignAdminRole(alice);
        Demographics demographic = createDefaultDemographics(alice.getId());


        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(alice.getWorkEmail(), ADMIN_ROLE);

        final Publisher<HttpResponse<List<DemographicsResponseDTO>>> response = client
                .exchange(request, Argument.listOf(DemographicsResponseDTO.class));

        StepVerifier.create(response)
                        .thenConsumeWhile(resp -> {
                            assertEquals(resp.getStatus(), HttpStatus.OK);
                            assertEquals(resp.body().get(0).getId(), demographic.getId());
                            assertEquals(resp.body().size(), 1);
                            return true;
                        })
                .expectComplete()
                .verify();

    }

    @Test
    public void testGETFindByValidGender() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        createAndAssignAdminRole(alice);
        Demographics demographic = createDefaultDemographics(alice.getId());

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?gender=%s", demographic.getGender()))
                .basicAuth(alice.getWorkEmail(), ADMIN_ROLE);

        HttpResponse<List<DemographicsResponseDTO>> response =  client.toBlocking()
                .exchange(request, Argument.listOf(DemographicsResponseDTO.class));


        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body().get(0).getId(), demographic.getId());
        assertEquals(response.body().size(), 1);
    }

    @Test
    public void testGETFindByWrongNameReturnsEmptyBody() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        createAndAssignAdminRole(alice);
        Demographics demographic = createDefaultDemographics(alice.getId());

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?gender=%s", encodeValue("random")))
                .basicAuth(alice.getWorkEmail(), ADMIN_ROLE);

        HttpResponse<List<DemographicsResponseDTO>> response =  client.toBlocking()
                .exchange(request, Argument.listOf(DemographicsResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(response.body(), new ArrayList<>());
    }

    @Test
    public void testPOSTCreateADemographics() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        createAndAssignAdminRole(alice);

        DemographicsCreateDTO newDemographics = new DemographicsCreateDTO();
        newDemographics.setMemberId(alice.getId());
        newDemographics.setGender("female");
        newDemographics.setIndustryTenure(5);

        final HttpRequest<DemographicsCreateDTO> request = HttpRequest.
                POST("/", newDemographics).basicAuth(alice.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<DemographicsResponseDTO> response = client.toBlocking().exchange(request,DemographicsResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(newDemographics.getGender(), response.body().getGender());
    }

    @Test
    public void testPOSTCreateADemographicsNoName() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        createAndAssignAdminRole(alice);

        DemographicsCreateDTO newDemographics = new DemographicsCreateDTO();

        final HttpRequest<DemographicsCreateDTO> request = HttpRequest.
                POST("/", newDemographics).basicAuth(alice.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTSuccessfulUpdate() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        createAndAssignAdminRole(lucy);

        Demographics demographicToUpdate = createDefaultDemographics(lucy.getId());
        DemographicsUpdateDTO updatedDemographics = new DemographicsUpdateDTO();
        updatedDemographics.setGender("female");
        updatedDemographics.setId(demographicToUpdate.getId());
        updatedDemographics.setMemberId(demographicToUpdate.getMemberId());

        final HttpRequest<DemographicsUpdateDTO> request = HttpRequest.
                PUT("/", updatedDemographics).basicAuth(lucy.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<DemographicsResponseDTO> response = client.toBlocking().exchange(request, DemographicsResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), updatedDemographics.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    public void testPUTWrongId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        createAndAssignAdminRole(lucy);

        Demographics demographicToUpdate = createDefaultDemographics(lucy.getId());
        DemographicsUpdateDTO updatedDemographics = new DemographicsUpdateDTO();
        updatedDemographics.setGender("female");
        updatedDemographics.setId(UUID.randomUUID());
        updatedDemographics.setMemberId(demographicToUpdate.getMemberId());

        final HttpRequest<DemographicsUpdateDTO> request = HttpRequest.
                PUT("/", updatedDemographics).basicAuth(lucy.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(responseException.getMessage(), String.format("Demographics %s does not exist, cannot update", updatedDemographics.getId()));
    }

    @Test
    public void testPUTNoId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        createAndAssignAdminRole(lucy);

        DemographicsUpdateDTO updatedDemographics = new DemographicsUpdateDTO();

        final HttpRequest<DemographicsUpdateDTO> request = HttpRequest.
                PUT("/", updatedDemographics).basicAuth(lucy.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(responseException.getMessage(), "Bad Request");
    }

    @Test
    public void testDELETEDemographics() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        createAndAssignAdminRole(lucy);

        Demographics demographic = createDefaultDemographics(lucy.getId());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", demographic.getId())).basicAuth(lucy.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<Boolean> response = client.toBlocking().exchange(request, Boolean.class);

        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testDELETEDemographicsWrongId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        createAndAssignAdminRole(lucy);

        Demographics demographic = createDefaultDemographics(lucy.getId());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", UUID.randomUUID())).basicAuth(lucy.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    public void testDELETEDemographicsNoPermission() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        createAndAssignRole(RoleType.MEMBER, lucy);

        Demographics demographic = createDefaultDemographics(lucy.getId());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", demographic.getId())).basicAuth(lucy.getWorkEmail(),MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("Requires admin privileges", responseException.getMessage());
        assertEquals(HttpStatus.FORBIDDEN,responseException.getStatus());

    }
    
}
