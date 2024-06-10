package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Singleton
class VolunteeringServiceImpl implements VolunteeringService {

    private static final Logger LOG = LoggerFactory.getLogger(VolunteeringServiceImpl.class);
    private static final String ORG_NAME_ALREADY_EXISTS_MESSAGE = "Volunteering Organization with name %s already exists";

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
        if (organization.getId() != null) {
            return update(organization);
        }
        // Fail if a certification with the same name already exists
        validate(organizationRepo.getByName(organization.getName()).isPresent(),
                ORG_NAME_ALREADY_EXISTS_MESSAGE,
                organization.getName());
        return organizationRepo.save(organization);
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
        // Fail if a certification with the same name already exists (but it's not this one)
        validate(organizationRepo.getByName(organization.getName())
                        .map(c -> !c.getId().equals(organization.getId())).orElse(false),
                ORG_NAME_ALREADY_EXISTS_MESSAGE,
                organization.getName());
        return organizationRepo.update(organization);
    }

    @Override
    public VolunteeringRelationship update(VolunteeringRelationship organization) {
        return null;
    }

    @Override
    public VolunteeringEvent update(VolunteeringEvent organization) {
        return null;
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}
