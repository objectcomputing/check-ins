package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
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

    private final ActionItemRepository actionItemRepo;
    private final CheckInServices checkInServices;
    private final MemberProfileServices memberServices;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;

    public PermissionsValidation(ActionItemRepository actionItemRepo, CheckInServices checkInServices,
                                 MemberProfileServices memberServices, SecurityService securityService,
                                 CurrentUserServices currentUserServices) {
        this.actionItemRepo = actionItemRepo;
        this.checkInServices = checkInServices;
        this.memberServices = memberServices;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }

    public void validatePermissions(@NotNull boolean isError, @NotNull String message, Object... args) {

        if (isError) {
            throw new PermissionException(String.format(message, args));
        }
    }

    public void validateActionItemPermissions(@Valid @NotNull ActionItem actionItem) {

        CurrentUserInfo currentUserInfo = new CurrentUserInfo();

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();
        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

        if (!currentUserInfo.isAdmin && isCompleted) {
            validatePermissions(true, "User is unauthorized to do this operation");
        } else if (!currentUserInfo.isAdmin && !isCompleted) {
            validatePermissions(!currentUserInfo.currentUser.getId().equals(pdlId) && !currentUserInfo.currentUser.getId().equals(teamMemberId), "User is unauthorized to do this operation");
        }

    }

    public void validateActionItemPermissionsForRead(@Valid @NotNull ActionItem actionItem) {

        CurrentUserInfo currentUserInfo = new CurrentUserInfo();

        if (!currentUserInfo.isAdmin) {
            CheckIn checkinRecord = checkInServices.read(actionItem.getCheckinid());
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validatePermissions(!currentUserInfo.currentUser.getId().equals(pdlId) && !currentUserInfo.currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

    }

    public void validateActionItemPermissionsForUpdate(@Valid @NotNull ActionItem actionItem) {

        CurrentUserInfo currentUserInfo = new CurrentUserInfo();

        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createdById = actionItem.getCreatedbyid();

            CheckIn checkinRecord = checkInServices.read(checkinId);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

            if (!currentUserInfo.isAdmin && isCompleted) {
                validatePermissions(true, "User is unauthorized to do this operation");
            } else if (!currentUserInfo.isAdmin && !isCompleted) {
                validatePermissions(!currentUserInfo.currentUser.getId().equals(pdlId) && !currentUserInfo.currentUser.getId().equals(createdById), "User is unauthorized to do this operation");
            }

        }

    }

    public void validateActionItemPermissionsForFindByFields(UUID checkinid, UUID createdbyid) {

        CurrentUserInfo currentUserInfo = new CurrentUserInfo();

        if (checkinid != null) {
            CheckIn checkinRecord = checkInServices.read(checkinid);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validatePermissions(!currentUserInfo.currentUser.getId().equals(pdlId) &&
                    !currentUserInfo.currentUser.getId().equals(teamMemberId) &&
                    !currentUserInfo.isAdmin, "User is unauthorized to do this operation");
        } else if (createdbyid != null) {
            MemberProfile memberRecord = memberServices.getById(createdbyid);
            validatePermissions(!currentUserInfo.currentUser.getId().equals(memberRecord.getId()) &&
                    !currentUserInfo.isAdmin, "User is unauthorized to do this operation");
        } else {
            validatePermissions(!currentUserInfo.isAdmin, "User is unauthorized to do this operation");
        }

    }

    public void validateActionItemPermissionsForDelete(@NotNull UUID id) {

        CurrentUserInfo currentUserInfo = new CurrentUserInfo();

        ActionItem actionItem = actionItemRepo.findById(id).orElse(null);
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

        if (!currentUserInfo.isAdmin && isCompleted) {
            validatePermissions(true, "User is unauthorized to do this operation");
        } else if (!currentUserInfo.isAdmin && !isCompleted) {
            validatePermissions(!currentUserInfo.currentUser.getId().equals(pdlId) && !currentUserInfo.currentUser.getId().equals(createdById), "User is unauthorized to do this operation");
        }

    }

    class CurrentUserInfo {
        String workEmail;
        MemberProfile currentUser;
        Boolean isAdmin;

        public CurrentUserInfo() {
            this.workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
            this.currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
            this.isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);
        }

    }
}
