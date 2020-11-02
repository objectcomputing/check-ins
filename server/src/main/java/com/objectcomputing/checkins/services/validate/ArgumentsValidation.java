package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.action_item.ActionItem;
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

    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;

    public ArgumentsValidation(CheckInServices checkInServices, MemberProfileServices memberServices) {
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
    }

    public void validateArguments(@NotNull boolean isError, @NotNull String message, Object... args) {

        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

    public void validateActionItemArguments(@Valid ActionItem actionItem) {
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();
        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

        validateArguments(actionItem.getId() != null, "Found unexpected id %s for action item", actionItem.getId());
        validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

//        if (isError) {
//            throw new BadArgException(String.format(message, args));
//        }
    }

//    public void validatePermissions(@NotNull boolean isError, @NotNull String message, Object... args) {
//        if (isError) {
//            throw new PermissionException(String.format(message, args));
//        }
//    }


}
