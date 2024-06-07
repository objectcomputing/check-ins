package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface VolunteeringService {

    List<VolunteeringOrganization> listOrganizations(boolean includeDeactivated);

    List<VolunteeringRelationship> listRelationships(@Nullable UUID memberId, @Nullable UUID organizationId, boolean includeDeactivated);

    List<VolunteeringEvent> listEvents(@Nullable UUID memberId, @Nullable UUID relationshipId, boolean includeDeactivated);

    VolunteeringOrganization create(VolunteeringOrganization organization);

    VolunteeringRelationship create(VolunteeringRelationship organization);

    VolunteeringEvent create(VolunteeringEvent organization);

    VolunteeringOrganization update(VolunteeringOrganization organization);

    VolunteeringRelationship update(VolunteeringRelationship organization);

    VolunteeringEvent update(VolunteeringEvent organization);
}
