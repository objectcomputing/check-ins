package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ActionItemCRUDValidator implements CRUDValidator<ActionItem> {

    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;
    private final ArgumentsValidation argumentsValidation;
    private final ActionItemRepository actionItemRepo;

    @Inject
    public ActionItemCRUDValidator(CheckInServices checkInServices, MemberProfileServices memberServices, ArgumentsValidation argumentsValidation, ActionItemRepository actionItemRepo) {
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
        this.argumentsValidation = argumentsValidation;
        this.actionItemRepo = actionItemRepo;
    }

    @Override
    public void validateCreate(@Valid @NotNull ActionItem actionItem) {

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(actionItem.getId() != null, "Found unexpected id %s for action item", actionItem.getId());
        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }

    @Override
    public void validateRead(@Valid @NotNull ActionItem actionItem) {

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }

    @Override
    public void validateUpdate(@Valid @NotNull ActionItem actionItem) {

        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createdById = actionItem.getCreatedbyid();

            argumentsValidation.validateArguments(checkinId == null || createdById == null, "Invalid action item %s", actionItem);
            argumentsValidation.validateArguments(id == null || actionItemRepo.findById(id).isEmpty(), "Unable to locate action item to update with id %s", actionItem.getId());
            argumentsValidation.validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
            argumentsValidation.validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);
        }

    }

    @Override
    public void validateDelete(@Valid @NotNull ActionItem actionItem) {

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(checkinId == null || createdById == null, "Invalid action item %s", actionItem);
        argumentsValidation.validateArguments(actionItem.getId() == null || actionItemRepo.findById(actionItem.getId()).isEmpty(), "Unable to locate action item to delete with id %s", actionItem.getId());
        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }

}
