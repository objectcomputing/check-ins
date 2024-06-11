package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
class VolunteeringServiceImpl implements VolunteeringService {

    private static final String ORG_NAME_ALREADY_EXISTS_MESSAGE = "Volunteering Organization with name %s already exists";

    private final MemberProfileRepository memberProfileRepository;
    private final CurrentUserServices currentUserServices;
    private final RolePermissionServices rolePermissionServices;
    private final VolunteeringOrganizationRepository organizationRepo;
    private final VolunteeringRelationshipRepository relationshipRepo;
    private final VolunteeringEventRepository eventRepo;

    VolunteeringServiceImpl(
            MemberProfileRepository memberProfileRepository,
            CurrentUserServices currentUserServices,
            RolePermissionServices rolePermissionServices,
            VolunteeringOrganizationRepository organizationRepo,
            VolunteeringRelationshipRepository relationshipRepo,
            VolunteeringEventRepository eventRepo
    ) {
        this.memberProfileRepository = memberProfileRepository;
        this.currentUserServices = currentUserServices;
        this.rolePermissionServices = rolePermissionServices;
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
    public List<VolunteeringEvent> listEvents(@Nullable UUID memberId, @Nullable UUID organizationId, boolean includeDeactivated) {
        if (memberId != null && organizationId != null) {
            return eventRepo.findByMemberIdAndOrganizationId(memberId, organizationId, includeDeactivated);
        } else if (memberId != null) {
            return eventRepo.findByMemberId(memberId, includeDeactivated);
        } else if (organizationId != null) {
            return eventRepo.findByOrganizationId(organizationId, includeDeactivated);
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
    public VolunteeringRelationship create(VolunteeringRelationship relationship) {
        if (relationship.getId() != null) {
            return update(relationship);
        }
        validateRelationship(relationship, "create");
        return relationshipRepo.save(relationship);
    }

    @Override
    public VolunteeringEvent create(VolunteeringEvent event) {
        if (event.getId() != null) {
            return update(event);
        }
        validateEvent(event, "create");
        return eventRepo.save(event);
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
    public VolunteeringRelationship update(VolunteeringRelationship relationship) {
        validateRelationship(relationship, "update");
        return relationshipRepo.update(relationship);
    }

    @Override
    public VolunteeringEvent update(VolunteeringEvent event) {
        validateEvent(event, "update");
        return eventRepo.update(event);
    }

    @Override
    public void deleteEvent(UUID id) {
        VolunteeringEvent event = eventRepo.findById(id).orElse(null);
        if (event == null) {
            return;
        }
        validateEvent(event, "delete");
        eventRepo.deleteById(id);
    }

    private void validateRelationship(VolunteeringRelationship relationship, String action) {
        validate(memberProfileRepository.findById(relationship.getMemberId()).isEmpty(), "Member %s doesn't exist", relationship.getMemberId());
        validate(organizationRepo.findById(relationship.getOrganizationId()).isEmpty(), "Volunteering organization %s doesn't exist", relationship.getOrganizationId());
        validatePermission(relationship, action);
    }

    private void validateEvent(VolunteeringEvent event, String action) {
        validate(relationshipRepo.findById(event.getRelationshipId()).isEmpty(), "Volunteering relationship %s doesn't exist", event.getRelationshipId());
        validate(event.getHours() < 0, "Hours must be non-negative");
        validatePermission(event, action);
    }

    private void validatePermission(VolunteeringRelationship relationship, String action) {
        // Fail if the user doesn't have permission to modify the relationship
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean hasPermission = rolePermissionServices.findUserPermissions(currentUserId).contains(Permission.CAN_ADMINISTER_VOLUNTEERING_RELATIONSHIPS);
        // Be sure to go and check the member id from the relationship in the DB, not the one passed in here
        UUID memberId = relationship.getId() == null
                ? relationship.getMemberId()
                : relationshipRepo.findById(relationship.getId()).map(VolunteeringRelationship::getMemberId).orElseThrow(() -> new BadArgException("Unknown member %s".formatted(relationship.getMemberId())));
        boolean ownersRelationship = memberId.equals(currentUserId);
        validate(!hasPermission && !ownersRelationship, "Member %s does not have permission to %s Volunteering relationship for member %s", currentUserId, action, memberId);
    }

    private void validatePermission(VolunteeringEvent event, String action) {
        // Fail if the user doesn't have permission to modify the event
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean hasPermission = rolePermissionServices.findUserPermissions(currentUserId).contains(Permission.CAN_ADMINISTER_VOLUNTEERING_EVENTS);
        // Be sure to go and check the event in the DB, not the one passed in here
        UUID relationshipId = event.getId() == null
                ? event.getRelationshipId()
                : eventRepo.findById(event.getId()).map(VolunteeringEvent::getRelationshipId).orElseThrow(() -> new BadArgException("Unknown relationship %s".formatted(event.getRelationshipId())));
        boolean ownersRelationship = relationshipRepo.findById(relationshipId)
                .map(r -> r.getMemberId().equals(currentUserId))
                .orElse(false);
        validate(!hasPermission && !ownersRelationship, "Member %s does not have permission to %s Volunteering event for relationship %s", currentUserId, action, relationshipId);
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}
