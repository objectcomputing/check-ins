package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
class VolunteeringServiceImpl implements VolunteeringService {

    private final VolunteeringOrganizationRepository organizationRepo;
    private final VolunteeringRelationshipRepository relationshipRepo;
    private final VolunteeringEventRepository eventRepo;

    VolunteeringServiceImpl(
            VolunteeringOrganizationRepository organizationRepo,
            VolunteeringRelationshipRepository relationshipRepo,
            VolunteeringEventRepository eventRepo
    ) {
        this.organizationRepo = organizationRepo;
        this.relationshipRepo = relationshipRepo;
        this.eventRepo = eventRepo;
    }

    @Override
    public List<VolunteeringOrganization> listOrganizations(boolean includeDeactivated) {
        return organizationRepo.findAll(includeDeactivated);
    }

    @Override
    public List<VolunteeringRelationship> listRelationships(UUID memberId, UUID organizationId, boolean includeDeactivated) {
        if (memberId != null && organizationId != null) {
            return relationshipRepo.findByMemberIdAndOrganizationId(memberId, organizationId, includeDeactivated);
        } else if (memberId != null) {
            return relationshipRepo.findByMemberId(memberId, includeDeactivated);
        } else if (organizationId != null) {
            return relationshipRepo.findByOrganizationId(organizationId, includeDeactivated);
        } else {
            return relationshipRepo.findAll(includeDeactivated);
        }
    }

    @Override
    public List<VolunteeringEvent> listEvents(@Nullable UUID memberId, @Nullable UUID relationshipId, boolean includeDeactivated) {
        if (memberId != null) {
            return eventRepo.findByMemberId(memberId, includeDeactivated);
        } else if (relationshipId != null) {
            return eventRepo.findByRelationshipId(relationshipId, includeDeactivated);
        } else {
            return eventRepo.findAll(includeDeactivated);
        }
    }

    @Override
    public VolunteeringOrganization create(VolunteeringOrganization organization) {
        return null;
    }

    @Override
    public VolunteeringRelationship create(VolunteeringRelationship organization) {
        return null;
    }

    @Override
    public VolunteeringEvent create(VolunteeringEvent organization) {
        return null;
    }

    @Override
    public VolunteeringOrganization update(VolunteeringOrganization organization) {
        return null;
    }

    @Override
    public VolunteeringRelationship update(VolunteeringRelationship organization) {
        return null;
    }

    @Override
    public VolunteeringEvent update(VolunteeringEvent organization) {
        return null;
    }
}
