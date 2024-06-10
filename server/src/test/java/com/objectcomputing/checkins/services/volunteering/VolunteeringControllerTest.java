package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.VolunteeringFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VolunteeringControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, VolunteeringFixture {

    @Inject
    @Client("/services/volunteer")
    HttpClient httpClient;

    BlockingHttpClient client;

    @BeforeEach
    void makeRoles() {
        client = httpClient.toBlocking();
        createAndAssignRoles();
    }

    @Test
    void testCreateOrganization() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        List<VolunteeringOrganization> list = client.retrieve(HttpRequest.GET("/organization").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(VolunteeringOrganization.class));
        assertEquals(0, list.size());

        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", "website");
        HttpResponse<VolunteeringOrganization> response = client.exchange(HttpRequest.POST("/organization", org).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE), VolunteeringOrganization.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body().getId());
        assertEquals("name", response.body().getName());
        assertEquals("description", response.body().getDescription());
        assertEquals("website", response.body().getWebsite());

        // List works as member without the profile
        list = client.retrieve(HttpRequest.GET("/organization").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(VolunteeringOrganization.class));
        assertEquals(1, list.size());
        assertEquals("name", list.getFirst().getName());
        assertEquals("description", list.getFirst().getDescription());
        assertEquals("website", list.getFirst().getWebsite());
        assertTrue(list.getFirst().isActive(), "Organization should be active by default");
    }

    @Test
    void organizationsCanBeInactive() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createVolunteeringOrganization("alpha", "alpha desc", "https://alpha.com");
        createVolunteeringOrganization("gamma", "gamma desc", "https://gamma.com");
        createVolunteeringOrganization("epsilon", "epsilon desc", "https://epsilon.com");
        createVolunteeringOrganization("beta", "beta desc", "https://beta.com", false);

        // List by default hides inactive (and they're ordered by name)
        List<VolunteeringOrganization> list = client.retrieve(HttpRequest.GET("/organization").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(VolunteeringOrganization.class));
        assertEquals(3, list.size());
        assertEquals(List.of("alpha", "epsilon", "gamma"), list.stream().map(VolunteeringOrganization::getName).toList());

        // List with includeDeactivated shows all (and they're ordered by name)
        list = client.retrieve(HttpRequest.GET("/organization?includeDeactivated=true").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE), Argument.listOf(VolunteeringOrganization.class));
        assertEquals(4, list.size());
        assertEquals(List.of("alpha", "beta", "epsilon", "gamma"), list.stream().map(VolunteeringOrganization::getName).toList());
    }

    @Test
    void testCreateOrganizationWithoutRole() {
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.exchange(HttpRequest.POST("/organization", org).basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void testCreateOrganizationWithoutDuplicateName() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createVolunteeringOrganization("name", "description", "website");
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.exchange(HttpRequest.POST("/organization", org).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE)));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Volunteering Organization with name name already exists", e.getMessage());
    }

    @Test
    void testCreateOrganizationWithoutName() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO(null, "description", "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.exchange(HttpRequest.POST("/organization", org).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE)));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("organization.name: must not be blank"), body + " should contain 'organization.name: must not be blank'");
    }

    @Test
    void testCreateOrganizationWithoutDescription() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", null, "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.exchange(HttpRequest.POST("/organization", org).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE)));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("organization.description: must not be blank"), body + " should contain 'organization.description: must not be blank'");
    }

    @Test
    void testCreateOrganizationWithoutWebsite() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", null);

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.exchange(HttpRequest.POST("/organization", org).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE)));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("organization.website: must not be blank"), body + " should contain 'organization.website: must not be blank'");
    }
}
