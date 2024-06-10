package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.volunteering.VolunteeringOrganization;

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
}
