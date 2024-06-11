package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.VolunteeringFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = VolunteeringClients.Event.ENABLED, value = "true")
class VolunteeringEventControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, VolunteeringFixture {

    @Inject
    VolunteeringClients.Event eventClient;

    private final static String MEMBER_AUTH = auth(MEMBER_ROLE, MEMBER_ROLE);

    static private String auth(String email, String role) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + role).getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void makeRoles() {
        createAndAssignRoles();
    }

    @Test
    void startsEmpty() {
        var list = eventClient.list(MEMBER_AUTH);
        assertTrue(list.isEmpty());
    }

    @Test
    void memberCanCreateEventForTheirRelationships() {
        MemberProfile tim = createADefaultMemberProfile();
        String timAuth = auth(tim.getWorkEmail(), MEMBER_ROLE);
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        LocalDate now = LocalDate.now();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);

        var event = new VolunteeringEventDTO(relationship.getId(), now, 10, "Notes");
        var createdEvent = eventClient.create(timAuth, event);

        assertEquals(HttpStatus.CREATED, createdEvent.getStatus());
        var createdEventBody = createdEvent.body();
        assertNotNull(createdEventBody.getId());
        assertEquals(relationship.getId(), createdEventBody.getRelationshipId());
        assertEquals(now, createdEventBody.getEventDate());
        assertEquals(10, createdEventBody.getHours());
        assertEquals("Notes", createdEventBody.getNotes());
    }

    @Test
    void memberCannotCreateEventForSomeoneElseRelationships() {
        MemberProfile tim = createADefaultMemberProfile();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        LocalDate now = LocalDate.now();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);

        var event = new VolunteeringEventDTO(relationship.getId(), now, 10, "Notes");

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), MEMBER_ROLE);

        var createdEvent = assertThrows(HttpClientResponseException.class, () -> eventClient.create(bobAuth, event));
        assertEquals(HttpStatus.BAD_REQUEST, createdEvent.getStatus());
        assertEquals("Member %s does not have permission to create Volunteering event for relationship %s".formatted(bob.getId(), relationship.getId()), createdEvent.getMessage());
    }

    @Test
    void memberWithPermissionCanCreateEventForSomeoneElseRelationships() {
        MemberProfile tim = createADefaultMemberProfile();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        LocalDate now = LocalDate.now();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), ADMIN_ROLE);

        var event = new VolunteeringEventDTO(relationship.getId(), now, 10, "Notes");
        var createdEvent = eventClient.create(bobAuth, event);

        assertEquals(HttpStatus.CREATED, createdEvent.getStatus());
        var createdEventBody = createdEvent.body();
        assertNotNull(createdEventBody.getId());
        assertEquals(relationship.getId(), createdEventBody.getRelationshipId());
        assertEquals(now, createdEventBody.getEventDate());
        assertEquals(10, createdEventBody.getHours());
        assertEquals("Notes", createdEventBody.getNotes());
    }

    @Test
    void memberCanDeleteTheirOwnEvents() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        String timAuth = auth(tim.getWorkEmail(), MEMBER_ROLE);
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        var deletedEvent = eventClient.delete(timAuth, event.getId());
        assertEquals(HttpStatus.OK, deletedEvent.getStatus());
    }

    @Test
    void memberCannotDeleteOthersEvents() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), MEMBER_ROLE);

        var e = assertThrows(HttpClientResponseException.class, () -> eventClient.delete(bobAuth, event.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Member %s does not have permission to delete Volunteering event for relationship %s".formatted(bob.getId(), relationship.getId()), e.getMessage());
    }

    @Test
    void memberWithPermissionCanDeleteOthersEvents() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), ADMIN_ROLE);

        var deletedEvent = eventClient.delete(bobAuth, event.getId());
        assertEquals(HttpStatus.OK, deletedEvent.getStatus());
    }
}
