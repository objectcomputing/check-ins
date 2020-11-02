package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Singleton
public class PermissionsValidation {


    String workEmail;
    MemberProfile currentUser;
    Boolean isAdmin;

    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;

    public PermissionsValidation(CheckInServices checkInServices, MemberProfileServices memberServices, SecurityService securityService, CurrentUserServices currentUserServices) {
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }

    public void validatePermissions(@NotNull boolean isError, @NotNull String message, Object... args) {

        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

    public void validateActionItemPermissions(@Valid ActionItem actionItem) {

        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();
        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

        if (!isAdmin && isCompleted) {
            validatePermissions(true, "User is unauthorized to do this operation");
        } else if (!isAdmin && !isCompleted) {
            validatePermissions(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(teamMemberId), "User is unauthorized to do this operation");
        }

    }

    public void getCurrentUserInfo() {

        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

    }

}
