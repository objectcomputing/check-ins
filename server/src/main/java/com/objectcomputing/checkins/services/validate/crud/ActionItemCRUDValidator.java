package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Named("ActionItem")
public class ActionItemCRUDValidator implements CRUDValidator<ActionItem> {

    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;
    private final ArgumentsValidation argumentsValidation;
    private final PermissionsValidation permissionsValidation;
    private final ActionItemRepository actionItemRepo;
    private final CurrentUserServices currentUserServices;

    @Inject
    public ActionItemCRUDValidator(CheckInServices checkInServices, MemberProfileServices memberServices,
                                   ArgumentsValidation argumentsValidation, PermissionsValidation permissionsValidation,
                                   ActionItemRepository actionItemRepo, CurrentUserServices currentUserServices) {
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
        this.argumentsValidation = argumentsValidation;
        this.permissionsValidation = permissionsValidation;
        this.actionItemRepo = actionItemRepo;
        this.currentUserServices = currentUserServices;
    }

    public void validateArgumentsCreate(@Valid @NotNull ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(actionItem.getId() != null,
                "Found unexpected id %s for action item", actionItem.getId());
        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null,
                "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null,
                "Member %s doesn't exist", createdById);
    }

    @Override
    public void validateArgumentsRead(@Valid @NotNull ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null,
                "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null,
                "Member %s doesn't exist", createdById);
    }

    @Override
    public void validateArgumentsUpdate(@Valid @NotNull ActionItem actionItem) {
        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createdById = actionItem.getCreatedbyid();

            argumentsValidation.validateArguments(checkinId == null || createdById == null,
                    "Invalid action item %s", actionItem);
            argumentsValidation.validateArguments(id == null || actionItemRepo.findById(id).isEmpty(),
                    "Unable to locate action item to update with id %s", actionItem.getId());
            argumentsValidation.validateArguments(checkInServices.read(checkinId) == null,
                    "CheckIn %s doesn't exist", checkinId);
            argumentsValidation.validateArguments(memberServices.getById(createdById) == null,
                    "Member %s doesn't exist", createdById);
        }
    }

    @Override
    public void validateArgumentsDelete(@Valid @NotNull ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(checkinId == null || createdById == null,
                "Invalid action item %s", actionItem);
        argumentsValidation.validateArguments(actionItem.getId() == null || actionItemRepo.findById(actionItem.getId()).isEmpty(),
                "Unable to locate action item to delete with id %s", actionItem.getId());
        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null,
                "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null,
                "Member %s doesn't exist", createdById);
    }

    @Override
    public void validatePermissionsCreate(@Valid @NotNull ActionItem actionItem) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canUpdateAllCheckins = checkInServices.canUpdateAllCheckins(currentUserId);

        if (!canUpdateAllCheckins) {
            final UUID checkinId = actionItem.getCheckinid();
            CheckIn checkinRecord = checkInServices.read(checkinId);

            boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
            permissionsValidation.validatePermissions(isCompleted);

            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

            boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(teamMemberId);
            permissionsValidation.validatePermissions(!currentUserIsCheckinParticipant);
        }
    }

    @Override
    public void validatePermissionsRead(@Valid @NotNull ActionItem actionItem) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canViewAllCheckins = checkInServices.canViewAllCheckins(currentUserId);

        if (!canViewAllCheckins) {
            CheckIn checkinRecord = checkInServices.read(actionItem.getCheckinid());
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

            boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createById);
            permissionsValidation.validatePermissions(!currentUserIsCheckinParticipant);
        }
    }

    @Override
    public void validatePermissionsUpdate(@Valid @NotNull ActionItem actionItem) {
        if (actionItem != null) {
            final UUID currentUserId = currentUserServices.getCurrentUser().getId();
            boolean canUpdateAllCheckins = checkInServices.canUpdateAllCheckins(currentUserId);

            if (!canUpdateAllCheckins) {
                final UUID checkinId = actionItem.getCheckinid();
                CheckIn checkinRecord = checkInServices.read(checkinId);

                boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
                permissionsValidation.validatePermissions(isCompleted);

                final UUID createdById = actionItem.getCreatedbyid();
                final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

                boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createdById);
                permissionsValidation.validatePermissions(!currentUserIsCheckinParticipant);
            }
        }
    }

    @Override
    public void validatePermissionsFindByFields(UUID checkinId, UUID createdById) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if(!checkInServices.doesUserHaveViewAccess(currentUserId, checkinId, createdById)){
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
    }

    @Override
    public void validatePermissionsDelete(@NotNull ActionItem actionItem) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canUpdateAllCheckins = checkInServices.canUpdateAllCheckins(currentUserId);

        if (!canUpdateAllCheckins) {
            final UUID checkinId = actionItem.getCheckinid();
            CheckIn checkinRecord = checkInServices.read(checkinId);
            boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();

            permissionsValidation.validatePermissions(isCompleted);

            final UUID createdById = actionItem.getCreatedbyid();
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createdById);

            permissionsValidation.validatePermissions(!currentUserIsCheckinParticipant);
        }
    }
}
