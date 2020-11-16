package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Singleton
public class ArgumentsValidation {

    private final ActionItemRepository actionItemRepo;
    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;

    public ArgumentsValidation(ActionItemRepository actionItemRepo, CheckInServices checkInServices, MemberProfileServices memberServices) {
        this.actionItemRepo = actionItemRepo;
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
    }

    public void validateArguments(@NotNull boolean isError, @NotNull String message, Object... args) {

        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

    public void validateActionItemArgumentsForSave(@Valid ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();
        CheckIn checkinRecord = checkInServices.read(checkinId);

        validateArguments(actionItem.getId() != null, "Found unexpected id %s for action item", actionItem.getId());
        validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }

    public void validateActionItemArgumentsForRead(@Valid ActionItem actionItem, @NotNull UUID id) {
        validateArguments(actionItem == null, "ActionItem %s doesn't exist", id);

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }

    public void validateActionItemArgumentsForUpdate(@Valid ActionItem actionItem) {
        validateArguments(actionItem == null, "ActionItem doesn't exist");

        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createdById = actionItem.getCreatedbyid();

            validateArguments(checkinId == null || createdById == null, "Invalid action item %s", actionItem);
            validateArguments(id == null || actionItemRepo.findById(id).isEmpty(), "Unable to locate action item to update with id %s", actionItem.getId());
            validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
            validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);
        }
    }

    public void validateActionItemArgumentsForDelete(@NotNull UUID id) {

        ActionItem actionItem = actionItemRepo.findById(id).orElse(null);

        validateArguments(actionItem == null, "invalid action item %s", id);

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        validateArguments(checkinId == null || createdById == null, "Invalid action item %s", actionItem);
        validateArguments(id == null || actionItemRepo.findById(id).isEmpty(), "Unable to locate action item to delete with id %s", actionItem.getId());
        validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }


}
