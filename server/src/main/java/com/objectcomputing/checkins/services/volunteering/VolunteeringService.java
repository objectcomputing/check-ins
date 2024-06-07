package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface VolunteeringService {

    List<VolunteeringOrganization> listOrganizations(boolean showInactive);

    List<VolunteeringRelationship> listRelationships(@Nullable UUID memberId, @Nullable UUID organizationId, boolean showInactive);

    List<VolunteeringEvent> listEvents(@Nullable UUID relationshipId, @Nullable UUID memberId, @Nullable UUID organizationId, boolean showInactive);

    VolunteeringOrganization create(VolunteeringOrganization organization);

    VolunteeringRelationship create(VolunteeringRelationship organization);

    VolunteeringEvent create(VolunteeringEvent organization);

    VolunteeringOrganization update(VolunteeringOrganization organization);

    VolunteeringRelationship update(VolunteeringRelationship organization);

    VolunteeringEvent update(VolunteeringEvent organization);
}
