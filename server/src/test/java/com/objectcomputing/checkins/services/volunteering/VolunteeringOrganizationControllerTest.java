package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.VolunteeringFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = VolunteeringClients.Organization.ENABLED, value = "true")
class VolunteeringOrganizationControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, VolunteeringFixture {

    @Inject
    VolunteeringClients.Organization organizationClient;

    static private String auth(String email, String role) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + role).getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void makeRoles() {
        createAndAssignRoles();
    }

    @Test
    void startsEmpty() {
        var list = organizationClient.list(auth(MEMBER_ROLE, MEMBER_ROLE));
        assertTrue(list.isEmpty());
    }

    @Test
    void testCreateOrganization() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        List<VolunteeringOrganization> list = organizationClient.list(memberAuth);
        assertEquals(0, list.size());

        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", "website");
        HttpResponse<VolunteeringOrganization> response = organizationClient.createOrganization(memberAuth, org);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body().getId());
        assertEquals("name", response.body().getName());
        assertEquals("description", response.body().getDescription());
        assertEquals("website", response.body().getWebsite());

        // List works as member without the profile
        list = organizationClient.list(memberAuth);
        assertEquals(1, list.size());
        assertEquals("name", list.getFirst().getName());
        assertEquals("description", list.getFirst().getDescription());
        assertEquals("website", list.getFirst().getWebsite());
        assertTrue(list.getFirst().isActive(), "Organization should be active by default");
    }

    @Test
    void organizationsCanBeInactive() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        createVolunteeringOrganization("alpha", "alpha desc", "https://alpha.com");
        createVolunteeringOrganization("gamma", "gamma desc", "https://gamma.com");
        createVolunteeringOrganization("epsilon", "epsilon desc", "https://epsilon.com");
        createVolunteeringOrganization("beta", "beta desc", "https://beta.com", false);

        // List by default hides inactive (and they're ordered by name)
        List<VolunteeringOrganization> list = organizationClient.list(memberAuth);
        assertEquals(3, list.size());
        assertEquals(List.of("alpha", "epsilon", "gamma"), list.stream().map(VolunteeringOrganization::getName).toList());

        // List with includeDeactivated shows all (and they're ordered by name)
        list = organizationClient.list(memberAuth, true);
        assertEquals(4, list.size());
        assertEquals(List.of("alpha", "beta", "epsilon", "gamma"), list.stream().map(VolunteeringOrganization::getName).toList());
    }

    @Test
    void cannotUpdateWithoutPermission() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        var org = createVolunteeringOrganization("name", "description", "website");
        var update = new VolunteeringOrganizationDTO(org.getName(), "new description", "new website");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> organizationClient.updateOrganization(memberAuth, org.getId(), update));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void cannotCreateDuplicateNamedOrganization() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        createVolunteeringOrganization("name", "description", "website");
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> organizationClient.createOrganization(adminAuth, org));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Volunteering Organization with name name already exists", e.getMessage());
    }

    @Test
    void cannotRenameWithDuplicateName() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);

        createVolunteeringOrganization("first", "desc", "web");
        VolunteeringOrganization second = createVolunteeringOrganization("second", "description", "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> organizationClient.updateOrganization(adminAuth, second.getId(), new VolunteeringOrganizationDTO("first", "desc", "web")));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Volunteering Organization with name first already exists", e.getMessage());
    }

    @Test
    void testCreateOrganizationWithoutName() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO(null, "description", "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> organizationClient.createOrganization(adminAuth, org));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("organization.name: must not be blank"), body + " should contain 'organization.name: must not be blank'");
    }

    @Test
    void testCreateOrganizationWithoutDescription() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", null, "website");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> organizationClient.createOrganization(adminAuth, org));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("organization.description: must not be blank"), body + " should contain 'organization.description: must not be blank'");
    }

    @Test
    void testCreateOrganizationWithoutWebsite() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        VolunteeringOrganizationDTO org = new VolunteeringOrganizationDTO("name", "description", null);

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> organizationClient.createOrganization(adminAuth, org));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("organization.website: must not be blank"), body + " should contain 'organization.website: must not be blank'");
    }
}
