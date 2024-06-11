package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.volunteering.VolunteeringOrganization;
import com.objectcomputing.checkins.services.volunteering.VolunteeringRelationship;

import java.time.LocalDate;
import java.util.UUID;

public interface VolunteeringFixture extends RepositoryFixture {

    default VolunteeringOrganization createDefaultVolunteeringOrganization() {
        return createVolunteeringOrganization(
                "Test Organization",
                "Test Description",
                "www.test.com"
        );
    }

    default VolunteeringOrganization createVolunteeringOrganization(String name, String description, String website) {
        return createVolunteeringOrganization(name, description, website, true);
    }

    default VolunteeringOrganization createVolunteeringOrganization(String name, String description, String website, boolean active) {
        return getVolunteeringOrganizationRepository().save(new VolunteeringOrganization(name, description, website, active));
    }

    default VolunteeringRelationship createVolunteeringRelationship(UUID memberId, UUID organizationId, LocalDate startDate) {
        return createVolunteeringRelationship(memberId, organizationId, startDate, null, true);
    }

    default VolunteeringRelationship createVolunteeringRelationship(UUID memberId, UUID organizationId, LocalDate startDate, boolean active) {
        return createVolunteeringRelationship(memberId, organizationId, startDate, null, active);
    }

    default VolunteeringRelationship createVolunteeringRelationship(UUID memberId, UUID organizationId, LocalDate startDate, LocalDate endDate) {
        return createVolunteeringRelationship(memberId, organizationId, startDate, endDate, true);
    }

    default VolunteeringRelationship createVolunteeringRelationship(UUID memberId, UUID organizationId, LocalDate startDate, LocalDate endDate, boolean active) {
        return getVolunteeringRelationshipRepository().save(new VolunteeringRelationship(memberId, organizationId, startDate, endDate, active));
    }
}
