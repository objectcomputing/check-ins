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
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = VolunteeringClients.Relationship.ENABLED, value = "true")
class VolunteeringRelationshipControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, VolunteeringFixture {

    @Inject
    VolunteeringClients.Relationship relationshipClient;

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
        var list = relationshipClient.list(MEMBER_AUTH, null, null, null);
        assertTrue(list.isEmpty());
    }

    @Test
    void memberCanCreateRelationshipForSelf() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        LocalDate startDate = LocalDate.now();

        var relationship = new VolunteeringRelationshipDTO(memberProfile.getId(), organization.getId(), startDate, null);
        var createdRelationship = relationshipClient.create(auth(memberProfile.getWorkEmail(), MEMBER_ROLE), relationship);
        assertEquals(HttpStatus.CREATED, createdRelationship.getStatus());
        var createdRelationshipBody = createdRelationship.body();
        assertNotNull(createdRelationshipBody.getId());

        var list = relationshipClient.list(MEMBER_AUTH, null, null, null);
        assertEquals(1, list.size());
        var first = list.getFirst();
        assertEquals(memberProfile.getId(), first.getMemberId());
        assertEquals(relationship.getMemberId(), first.getMemberId());
        assertEquals(organization.getId(), first.getOrganizationId());
        assertEquals(startDate, first.getStartDate());
        assertNull(first.getEndDate());
        assertTrue(first.isActive());
    }

    @Test
    void memberCannotCreateRelationshipForSomeoneElse() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        LocalDate startDate = LocalDate.now();

        var relationship = new VolunteeringRelationshipDTO(sarah.getId(), organization.getId(), startDate, null);
        var e = assertThrows(HttpClientResponseException.class, () -> relationshipClient.create(memberAuth, relationship));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Member %s does not have permission to create Volunteering relationship for member %s".formatted(memberProfile.getId(), sarah.getId()), e.getMessage());
    }

    @Test
    void adminCanCreateRelationshipForAnyone() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        LocalDate startDate = LocalDate.now();

        var relationship = new VolunteeringRelationshipDTO(sarah.getId(), organization.getId(), startDate, null);
        var created = relationshipClient.create(adminAuth, relationship);
        assertEquals(HttpStatus.CREATED, created.getStatus());
        var createdBody = created.body();
        assertNotNull(createdBody.getId());
        assertEquals(createdBody.getMemberId(), sarah.getId());
        assertEquals(createdBody.getOrganizationId(), organization.getId());
        assertEquals(createdBody.getStartDate(), startDate);
        assertNull(createdBody.getEndDate());
        assertTrue(createdBody.isActive());
    }

    @Test
    void cannotUpdateOtherMembersRelationshipsWithoutPermission() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");

        LocalDate startDate = LocalDate.now();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(sarah.getId(), organization.getId(), startDate);

        VolunteeringRelationshipDTO updateDto = new VolunteeringRelationshipDTO(relationship.getMemberId(), relationship.getOrganizationId(), startDate.plusDays(1), LocalDate.now());

        var update = assertThrows(HttpClientResponseException.class, () -> relationshipClient.update(memberAuth, relationship.getId(), updateDto));
        assertEquals(HttpStatus.BAD_REQUEST, update.getStatus());
        assertEquals("Member %s does not have permission to update Volunteering relationship for member %s".formatted(memberProfile.getId(), sarah.getId()), update.getMessage());
    }

    @Test
    void cannotHackUpdateOtherMembersRelationshipsWithoutPermission() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");

        LocalDate startDate = LocalDate.now();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(sarah.getId(), organization.getId(), startDate);

        VolunteeringRelationshipDTO updateDto = new VolunteeringRelationshipDTO(memberProfile.getId(), relationship.getOrganizationId(), startDate.plusDays(1), LocalDate.now());

        var update = assertThrows(HttpClientResponseException.class, () -> relationshipClient.update(memberAuth, relationship.getId(), updateDto));
        assertEquals(HttpStatus.BAD_REQUEST, update.getStatus());
        assertEquals("Member %s does not have permission to update Volunteering relationship for member %s".formatted(memberProfile.getId(), sarah.getId()), update.getMessage());
    }

    @Test
    void cannotHackUpdateMyRelationshipsToOtherPeopleWithoutPermission() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");

        LocalDate startDate = LocalDate.now();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(memberProfile.getId(), organization.getId(), startDate);

        VolunteeringRelationshipDTO updateDto = new VolunteeringRelationshipDTO(sarah.getId(), relationship.getOrganizationId(), startDate.plusDays(1), LocalDate.now());

        var update = assertThrows(HttpClientResponseException.class, () -> relationshipClient.update(memberAuth, relationship.getId(), updateDto));
        assertEquals(HttpStatus.BAD_REQUEST, update.getStatus());
        assertEquals("Member %s does not have permission to update Volunteering relationship for member %s".formatted(memberProfile.getId(), sarah.getId()), update.getMessage());
    }

    @Test
    void canUpdateOtherMembersRelationshipsWithPermission() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String adminAuth = auth(memberProfile.getWorkEmail(), ADMIN_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");

        LocalDate startDate = LocalDate.now();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(sarah.getId(), organization.getId(), startDate);

        VolunteeringRelationshipDTO updateDto = new VolunteeringRelationshipDTO(relationship.getMemberId(), relationship.getOrganizationId(), startDate.plusDays(1), startDate.plusDays(3), false);

        VolunteeringRelationship updated = relationshipClient.update(adminAuth, relationship.getId(), updateDto);
        assertEquals(relationship.getId(), updated.getId());
        assertEquals(sarah.getId(), updated.getMemberId());
        assertEquals(organization.getId(), updated.getOrganizationId());
        assertEquals(startDate.plusDays(1), updated.getStartDate());
        assertEquals(startDate.plusDays(3), updated.getEndDate());
        assertFalse(updated.isActive());
    }

    @Test
    void canUpdateYourOwnRelationshipsWithoutPermission() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        LocalDate startDate = LocalDate.now();

        VolunteeringOrganization organization = createDefaultVolunteeringOrganization();
        VolunteeringRelationship relationship = createVolunteeringRelationship(memberProfile.getId(), organization.getId(), startDate);

        VolunteeringRelationshipDTO updateDto = new VolunteeringRelationshipDTO(relationship.getMemberId(), relationship.getOrganizationId(), startDate.plusDays(1), startDate.plusDays(3));

        VolunteeringRelationship updated = relationshipClient.update(memberAuth, relationship.getId(), updateDto);
        assertEquals(relationship.getId(), updated.getId());
        assertEquals(memberProfile.getId(), updated.getMemberId());
        assertEquals(organization.getId(), updated.getOrganizationId());
        assertEquals(startDate.plusDays(1), updated.getStartDate());
        assertEquals(startDate.plusDays(3), updated.getEndDate());
        assertTrue(updated.isActive());
    }

    @Test
    void canListAndFilterRelationships() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        String memberAuth = auth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        MemberProfile sarah = memberWithoutBoss("sarah");
        MemberProfile tim = memberWithoutBoss("tim");

        VolunteeringOrganization liftForLife = createVolunteeringOrganization("Lift for Life", "Educate, empower, uplift", "https://www.liftforlifeacademy.org");
        VolunteeringOrganization foodbank = createVolunteeringOrganization("St. Louis Area Foodbank", "Works with over 600 partners", "https://stlfoodbank.org/find-food/");

        VolunteeringRelationship sarahLiftForLife = createVolunteeringRelationship(sarah.getId(), liftForLife.getId(), LocalDate.now().minusDays(2));
        VolunteeringRelationship timLiftForLife = createVolunteeringRelationship(tim.getId(), liftForLife.getId(), LocalDate.now());
        VolunteeringRelationship timFoodbankInactive = createVolunteeringRelationship(tim.getId(), foodbank.getId(), LocalDate.now().minusDays(3), null, false);
        VolunteeringRelationship sarahFoodbank = createVolunteeringRelationship(sarah.getId(), foodbank.getId(), LocalDate.now().minusDays(10), LocalDate.now());

        // Can find all relationships in correct order
        List<VolunteeringRelationship> allRelationships = relationshipClient.list(memberAuth, null, null, null);
        assertEquals(3, allRelationships.size());
        assertEquals(List.of(sarahFoodbank.getId(), sarahLiftForLife.getId(), timLiftForLife.getId()), allRelationships.stream().map(VolunteeringRelationship::getId).toList());

        // Can include inactive relationships
        List<VolunteeringRelationship> allWithInactiveRelationships = relationshipClient.list(memberAuth, null, null, true);
        assertEquals(4, allWithInactiveRelationships.size());
        assertEquals(List.of(sarahFoodbank.getId(), timFoodbankInactive.getId(), sarahLiftForLife.getId(), timLiftForLife.getId()), allWithInactiveRelationships.stream().map(VolunteeringRelationship::getId).toList());

        // Can filter by memberId
        List<VolunteeringRelationship> timRelationships = relationshipClient.list(memberAuth, tim.getId(), null, null);
        assertEquals(1, timRelationships.size());
        assertEquals(List.of(timLiftForLife.getId()), timRelationships.stream().map(VolunteeringRelationship::getId).toList());

        // Can filter by organization
        List<VolunteeringRelationship> liftRelationships = relationshipClient.list(memberAuth, null, liftForLife.getId(), null);
        assertEquals(2, liftRelationships.size());
        assertEquals(List.of(sarahLiftForLife.getId(), timLiftForLife.getId()), liftRelationships.stream().map(VolunteeringRelationship::getId).toList());

        // Can filter by organization and inactive
        List<VolunteeringRelationship> foodRelationships = relationshipClient.list(memberAuth, null, foodbank.getId(), null);
        assertEquals(1, foodRelationships.size());
        assertEquals(List.of(sarahFoodbank.getId()), foodRelationships.stream().map(VolunteeringRelationship::getId).toList());
        foodRelationships = relationshipClient.list(memberAuth, null, foodbank.getId(), true);
        assertEquals(2, foodRelationships.size());
        assertEquals(List.of(sarahFoodbank.getId(), timFoodbankInactive.getId()), foodRelationships.stream().map(VolunteeringRelationship::getId).toList());

        // Can filter by member and organization
        List<VolunteeringRelationship> sarahLiftRelationships = relationshipClient.list(memberAuth, sarah.getId(), liftForLife.getId(), null);
        assertEquals(1, sarahLiftRelationships.size());
        assertEquals(List.of(sarahLiftForLife.getId()), sarahLiftRelationships.stream().map(VolunteeringRelationship::getId).toList());

        // Can filter by member and organization and inactive
        List<VolunteeringRelationship> timFood = relationshipClient.list(memberAuth, tim.getId(), foodbank.getId(), null);
        assertEquals(0, timFood.size());
        timFood = relationshipClient.list(memberAuth, tim.getId(), foodbank.getId(), true);
        assertEquals(1, timFood.size());
        assertEquals(List.of(timFoodbankInactive.getId()), timFood.stream().map(VolunteeringRelationship::getId).toList());
    }
}
