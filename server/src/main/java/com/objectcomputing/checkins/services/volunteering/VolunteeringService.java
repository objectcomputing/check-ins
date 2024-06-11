package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface VolunteeringService {

    List<VolunteeringOrganization> listOrganizations(boolean includeDeactivated);

    List<VolunteeringRelationship> listRelationships(@Nullable UUID memberId, @Nullable UUID organizationId, boolean includeDeactivated);

    List<VolunteeringEvent> listEvents(@Nullable UUID memberId, @Nullable UUID organizationId, boolean includeDeactivated);

    VolunteeringOrganization create(VolunteeringOrganization organization);

    VolunteeringRelationship create(VolunteeringRelationship relationship);

    VolunteeringEvent create(VolunteeringEvent event);

    VolunteeringOrganization update(VolunteeringOrganization organization);

    VolunteeringRelationship update(VolunteeringRelationship relationship);

    VolunteeringEvent update(VolunteeringEvent event);

    void deleteEvent(UUID id);
}
