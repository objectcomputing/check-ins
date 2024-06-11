package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.VolunteeringFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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

    @Inject
    @Client("/services/volunteer/event")
    HttpClient httpClient;

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
    void memberCanUpdateTheirOwnEvents() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        String timAuth = auth(tim.getWorkEmail(), MEMBER_ROLE);
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        var updated = eventClient.update(timAuth, event.getId(), new VolunteeringEventDTO(relationship.getId(), now, 5, "New notes"));
        assertEquals(event.getId(), updated.getId());
        assertEquals("New notes", updated.getNotes());
    }

    @Test
    void memberCannotUpdateOthersEvents() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), MEMBER_ROLE);

        var e = assertThrows(HttpClientResponseException.class, () -> eventClient.update(bobAuth, event.getId(), new VolunteeringEventDTO(relationship.getId(), now, 5, "New notes")));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Member %s does not have permission to update Volunteering event for relationship %s".formatted(bob.getId(), relationship.getId()), e.getMessage());
    }

    @Test
    void memberCannotHackUpdateOthersEventsWithTheirOwnRelationship() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), MEMBER_ROLE);
        VolunteeringRelationship bobsRelationship = createVolunteeringRelationship(bob.getId(), organization.getId(), now);

        var e = assertThrows(HttpClientResponseException.class, () -> eventClient.update(bobAuth, event.getId(), new VolunteeringEventDTO(bobsRelationship.getId(), now, 5, "New notes")));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Member %s does not have permission to update Volunteering event for relationship %s".formatted(bob.getId(), relationship.getId()), e.getMessage());
    }

    @Test
    void memberCanUpdateOthersEventsWithProperPermission() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        MemberProfile bob = memberWithoutBoss("bob");
        String bobAuth = auth(bob.getWorkEmail(), ADMIN_ROLE);

        var updated = eventClient.update(bobAuth, event.getId(), new VolunteeringEventDTO(relationship.getId(), now, 5, "New notes"));
        assertEquals(event.getId(), updated.getId());
        assertEquals("New notes", updated.getNotes());
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

    /**
     * /
     * ├── liftForLife
     * │   ├── aliceLiftForLife
     * │   │   ├── aliceLiftEvent1
     * │   │   └── aliceLiftEvent2
     * │   ├── bobLiftForLife (INACTIVE)
     * │   │   └── bobLiftEvent1
     * │   └── clairLiftForLife (INACTIVE)
     * │       └── clairLiftEvent1
     * ├── foodBank
     * │   ├── aliceFood
     * │   │   └── aliceFoodEvent1
     * │   └── clairFood
     * │       └── clairFoodEvent1
     * └── closedOrg (INACTIVE)
     *     ├── bobClosed (INACTIVE)
     *     │   └── bobClosedEvent1
     *     └── clairClosed
     *         └── clairClosedEvent1
     */
    @Test
    void eventListCanBeFiltered() {
        MemberProfile alice = memberWithoutBoss("alice");
        MemberProfile bob = memberWithoutBoss("bob");
        MemberProfile claire = memberWithoutBoss("clair");

        LocalDate now = LocalDate.now();

        var liftForLife = createVolunteeringOrganization("Lift for Life", "Educate, empower, uplift", "https://www.liftforlifeacademy.org");
        var foodBank = createVolunteeringOrganization("St. Louis Area Foodbank", "Works with over 600 partners", "https://stlfoodbank.org/find-food/");
        var closedOrg = createVolunteeringOrganization("Closed Organization", "No longer active", "https://example.com", false);

        var aliceLiftForLife = createVolunteeringRelationship(alice.getId(), liftForLife.getId(), now.minusDays(2));
        var bobLiftForLife = createVolunteeringRelationship(bob.getId(), liftForLife.getId(), now, null, false);
        var claireLiftForLife = createVolunteeringRelationship(claire.getId(), liftForLife.getId(), now.minusDays(3), null, false);
        var aliceFood = createVolunteeringRelationship(alice.getId(), foodBank.getId(), now.minusDays(20));
        var claireFood = createVolunteeringRelationship(claire.getId(), foodBank.getId(), now.minusDays(4), now);
        var bobClosed = createVolunteeringRelationship(bob.getId(), closedOrg.getId(), now.minusDays(100), now.minusDays(50), false);
        var clairClosed = createVolunteeringRelationship(claire.getId(), closedOrg.getId(), now.minusDays(1), now);

        var aliceLiftEvent1 = createVolunteeringEvent(aliceLiftForLife.getId(), now.minusDays(2), 10, "aliceLiftEvent1"); // 2 days ago
        var aliceLiftEvent2 = createVolunteeringEvent(aliceLiftForLife.getId(), now, 8, "aliceLiftEvent2"); // today
        var bobLiftEvent1 = createVolunteeringEvent(bobLiftForLife.getId(), now, 6, "bobLiftEvent1"); // today
        var clairLiftEvent1 = createVolunteeringEvent(claireLiftForLife.getId(), now.minusDays(3), 4, "clairLiftEvent1"); // 3 days ago
        var aliceFoodEvent1 = createVolunteeringEvent(aliceFood.getId(), now.minusDays(20), 2, "aliceFoodEvent1"); // 20 days ago
        var clairFoodEvent1 = createVolunteeringEvent(claireFood.getId(), now, 1, "clairFoodEvent1"); // today
        var bobClosedEvent1 = createVolunteeringEvent(bobClosed.getId(), now.minusDays(76), 10, "bobClosedEvent1"); // 76 days ago
        var clairClosedEvent1 = createVolunteeringEvent(clairClosed.getId(), now.minusDays(1), 0, "clairClosedEvent1"); // yesterday

        // List all events, sorted by event date and then by organization name
        var list = eventClient.list(MEMBER_AUTH);
        assertEquals(List.of(aliceFoodEvent1, aliceLiftEvent1, aliceLiftEvent2, clairFoodEvent1), list);

        // Can filter by member
        list = eventClient.list(MEMBER_AUTH, alice.getId(), null, null);
        assertEquals(List.of(aliceFoodEvent1, aliceLiftEvent1, aliceLiftEvent2), list);

        // Can filter by organization
        list = eventClient.list(MEMBER_AUTH, null, foodBank.getId(), null);
        assertEquals(List.of(aliceFoodEvent1, clairFoodEvent1), list);

        // Can filter by organization and member
        list = eventClient.list(MEMBER_AUTH, claire.getId(), foodBank.getId(), null);
        assertEquals(List.of(clairFoodEvent1), list);

        // Can include deactivated
        list = eventClient.list(MEMBER_AUTH, null, null, true);
        assertEquals(List.of(bobClosedEvent1, aliceFoodEvent1, clairLiftEvent1, aliceLiftEvent1, clairClosedEvent1, aliceLiftEvent2, bobLiftEvent1, clairFoodEvent1), list);

        // closedOrg is inactive, so no events should be returned
        list = eventClient.list(MEMBER_AUTH, null, closedOrg.getId(), false);
        assertTrue(list.isEmpty());

        // We can show events for inactive organizations
        list = eventClient.list(MEMBER_AUTH, null, closedOrg.getId(), true);
        assertEquals(List.of(bobClosedEvent1, clairClosedEvent1), list);

        // And we can limit to a specific member
        list = eventClient.list(MEMBER_AUTH, claire.getId(), closedOrg.getId(), true);
        assertEquals(List.of(clairClosedEvent1), list);
    }

    @Test
    void relationshipMustExist() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        String timAuth = auth(tim.getWorkEmail(), MEMBER_ROLE);
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");
        UUID randomId = UUID.randomUUID();

        VolunteeringEventDTO newEvent = new VolunteeringEventDTO(randomId, now, 10, "Notes");

        // Creating an event with a non-existent relationship should fail
        var e = assertThrows(HttpClientResponseException.class, () -> eventClient.create(timAuth, newEvent));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Volunteering relationship %s doesn't exist".formatted(randomId), e.getMessage());

        // Updating an event to have a non-existent relationship should fail
        e = assertThrows(HttpClientResponseException.class, () -> eventClient.update(timAuth, event.getId(), newEvent));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Volunteering relationship %s doesn't exist".formatted(randomId), e.getMessage());
    }

    @Test
    void eventDateMustBeSet() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        String timAuth = auth(tim.getWorkEmail(), MEMBER_ROLE);
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        VolunteeringEventDTO newEvent = new VolunteeringEventDTO(relationship.getId(), null, 10, "Notes");

        // Creating an event with a null date should fail
        var e = assertThrows(HttpClientResponseException.class, () -> eventClient.create(timAuth, newEvent));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("event.eventDate: must not be null"), body + " should contain 'event.eventDate: must not be null'");

        // Updating an event to have a null date should fail
        e = assertThrows(HttpClientResponseException.class, () -> eventClient.update(timAuth, event.getId(), newEvent));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("event.eventDate: must not be null"), body + " should contain 'event.eventDate: must not be null'");
    }

    @Test
    void hoursMustBeNonNegative() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();
        String timAuth = auth(tim.getWorkEmail(), MEMBER_ROLE);
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");

        VolunteeringEventDTO newEvent = new VolunteeringEventDTO(relationship.getId(), now, -1, "Notes");

        // Creating an event with negative hours should fail
        var e = assertThrows(HttpClientResponseException.class, () -> eventClient.create(timAuth, newEvent));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Hours must be non-negative", e.getMessage());

        // Updating an event to have negative hours should fail
        e = assertThrows(HttpClientResponseException.class, () -> eventClient.update(timAuth, event.getId(), newEvent));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Hours must be non-negative", e.getMessage());
    }

    @Test
    void hoursAreRequired() {
        LocalDate now = LocalDate.now();
        MemberProfile tim = createADefaultMemberProfile();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(tim.getId(), organization.getId(), now);
        VolunteeringEvent event = createVolunteeringEvent(relationship.getId(), now, 10, "Notes");
        String postBody = """
                {
                  "relationshipId": "%s",
                  "eventDate": "2024-06-01",
                  "notes": "Notes"
                }""".formatted(relationship.getId());

        var postRequest = HttpRequest.POST("/", postBody).basicAuth(tim.getWorkEmail(), MEMBER_ROLE);
        var e = assertThrows(HttpClientResponseException.class, () -> httpClient.toBlocking().exchange(postRequest, VolunteeringEvent.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("event.hours: must not be null"), body + " should contain 'event.hours: must not be null'");

        // Updating an event to have null hours should fail
        var putRequest = HttpRequest.PUT("/" + event.getId(), postBody).basicAuth(tim.getWorkEmail(), MEMBER_ROLE);
        e = assertThrows(HttpClientResponseException.class, () -> httpClient.toBlocking().exchange(putRequest, VolunteeringEvent.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains("event.hours: must not be null"), body + " should contain 'event.hours: must not be null'");
    }
}
