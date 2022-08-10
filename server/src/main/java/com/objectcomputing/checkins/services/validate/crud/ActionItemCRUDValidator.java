package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Named("ActionItem")
public class ActionItemCRUDValidator implements CRUDValidator<ActionItem> {

    private final CheckInServices checkInServices;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final ArgumentsValidation argumentsValidation;
    private final PermissionsValidation permissionsValidation;
    private final ActionItemRepository actionItemRepo;
    private final CurrentUserServices currentUserServices;


    @Inject
    public ActionItemCRUDValidator(CheckInServices checkInServices, MemberProfileRetrievalServices memberProfileRetrievalServices,
                                   ArgumentsValidation argumentsValidation, PermissionsValidation permissionsValidation,
                                   ActionItemRepository actionItemRepo, CurrentUserServices currentUserServices) {
        this.checkInServices = checkInServices;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
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
        argumentsValidation.validateArguments(memberProfileRetrievalServices.getById(createdById).isEmpty(),
                "Member %s doesn't exist", createdById);
    }

    @Override
    public void validateArgumentsRead(@Valid @NotNull ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null,
                "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberProfileRetrievalServices.getById(createdById).isEmpty(),
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
            argumentsValidation.validateArguments(memberProfileRetrievalServices.getById(createdById).isEmpty(),
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
        argumentsValidation.validateArguments(memberProfileRetrievalServices.getById(createdById).isEmpty(),
                "Member %s doesn't exist", createdById);
    }

    @Override
    public void validatePermissionsCreate(@Valid @NotNull ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (!isAdmin && isCompleted) {
            permissionsValidation.validatePermissions(true, "User is unauthorized to do this operation");
        } else if (!isAdmin && !isCompleted) {
            permissionsValidation.validatePermissions(!currentUser.getId().equals(pdlId)
                            && !currentUser.getId().equals(teamMemberId),
                    "User is unauthorized to do this operation");
        }
    }

    @Override
    public void validatePermissionsRead(@Valid @NotNull ActionItem actionItem) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (!isAdmin) {
            CheckIn checkinRecord = checkInServices.read(actionItem.getCheckinid());
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            permissionsValidation.validatePermissions(
                    !currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById),
                    "User is unauthorized to do this operation");
        }
    }

    @Override
    public void validatePermissionsUpdate(@Valid @NotNull ActionItem actionItem) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (actionItem != null) {
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createdById = actionItem.getCreatedbyid();

            CheckIn checkinRecord = checkInServices.read(checkinId);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

            if (!isAdmin && isCompleted) {
                permissionsValidation.validatePermissions(true, "User is unauthorized to do this operation");
            } else if (!isAdmin && !isCompleted) {
                permissionsValidation.validatePermissions(!currentUser.getId().equals(pdlId) &&
                        !currentUser.getId().equals(createdById), "User is unauthorized to do this operation");
            }
        }
    }

    @Override
    public void validatePermissionsFindByFields(UUID checkinid, UUID createdbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (checkinid != null) {
            permissionsValidation.validatePermissions(!checkInServices.accessGranted(checkinid, currentUser.getId()),
                    "Uawe");
        } else if (createdbyid != null) {
            MemberProfile memberRecord = memberProfileRetrievalServices.getById(createdbyid).orElseThrow(() -> {
                throw new BadArgException("Member %s does not exist", createdbyid);
            });
            permissionsValidation.validatePermissions(!currentUser.getId().equals(memberRecord.getId()) &&
                    !isAdmin, "User is unauthorized to do this operation");
        } else {
            permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        }
    }

    @Override
    public void validatePermissionsDelete(@NotNull ActionItem actionItem) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

        if (!isAdmin && isCompleted) {
            permissionsValidation.validatePermissions(true, "User is unauthorized to do this operation");
        } else if (!isAdmin && !isCompleted) {
            permissionsValidation.validatePermissions(!currentUser.getId().equals(pdlId)
                    && !currentUser.getId().equals(createdById), "User is unauthorized to do this operation");
        }
    }
}
