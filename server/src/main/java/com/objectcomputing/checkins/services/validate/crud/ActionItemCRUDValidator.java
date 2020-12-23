package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.UUID;

public class ActionItemCRUDValidator implements CRUDValidator<ActionItem> {

    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;
    private final ArgumentsValidation argumentsValidation;

    @Inject
    public ActionItemCRUDValidator(CheckInServices checkInServices, MemberProfileServices memberServices, ArgumentsValidation argumentsValidation) {
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
        this.argumentsValidation = argumentsValidation;
    }

    @Override
    public void validateCreate(@Valid ActionItem actionItem) {

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        argumentsValidation.validateArguments(actionItem.getId() != null, "Found unexpected id %s for action item", actionItem.getId());
        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

    }
}
