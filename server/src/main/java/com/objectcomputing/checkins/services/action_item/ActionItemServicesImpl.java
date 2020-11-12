package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {

    private final CheckInServices checkInServices;
    private final ActionItemRepository actionItemRepo;
    private final MemberProfileServices memberServices;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;
    private final ArgumentsValidation argumentsValidation;
    private final PermissionsValidation permissionsValidation;

    public ActionItemServicesImpl(CheckInServices checkInServices, ActionItemRepository actionItemRepo,
                                  MemberProfileServices memberServices, SecurityService securityService,
                                  CurrentUserServices currentUserServices, ArgumentsValidation argumentsValidation,
                                  PermissionsValidation permissionsValidation) {
        this.checkInServices = checkInServices;
        this.actionItemRepo = actionItemRepo;
        this.memberServices = memberServices;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
        this.argumentsValidation = argumentsValidation;
        this.permissionsValidation = permissionsValidation;
    }

    public ActionItem save(@Valid ActionItem actionItem) {
        ActionItem actionItemRet = null;
        ActionItem validActionItem = null;

        if (actionItem != null) {

            argumentsValidation.validateActionItemArgumentsForSave(actionItem);
            permissionsValidation.validateActionItemPermissions(actionItem);

            double lastDisplayOrder = 0;
            try {
                lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse(Double.valueOf(0));
            } catch (NullPointerException npe) {
                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
                //nothing needs to happen here.
            }
            actionItem.setPriority(lastDisplayOrder + 1);

            actionItemRet = actionItemRepo.save(actionItem);

        }

        return actionItemRet;

    }

    public ActionItem read(@NotNull UUID id) {

        ActionItem actionItemResult = actionItemRepo.findById(id).orElse(null);

        argumentsValidation.validateActionItemArgumentsForRead(actionItemResult, id);
        permissionsValidation.validateActionItemReadPermissions(actionItemResult);

        return actionItemResult;

    }

    public ActionItem update(ActionItem actionItem) {
        ActionItem actionItemRet = null;

        argumentsValidation.validateActionItemArgumentsForUpdate(actionItem);
        permissionsValidation.validateActionItemUpdatePermissions(actionItem);

        actionItemRet = actionItemRepo.update(actionItem);

        return actionItemRet;

    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        if (checkinid != null) {
            CheckIn checkinRecord = checkInServices.read(checkinid);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            permissionsValidation.validatePermissions(!currentUser.getId().equals(pdlId) &&
                    !currentUser.getId().equals(teamMemberId) &&
                    !isAdmin, "User is unauthorized to do this operation");
        } else if (createdbyid != null) {
            MemberProfile memberRecord = memberServices.getById(createdbyid);
            permissionsValidation.validatePermissions(!currentUser.getId().equals(memberRecord.getId()) &&
                    !isAdmin, "User is unauthorized to do this operation");
        } else {
            permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        }

        Set<ActionItem> actionItems = new LinkedHashSet<>(
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid)));

        return actionItems;

    }

    public void delete(@NotNull UUID id) {

        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);
        ActionItem actionItem = actionItemRepo.findById(id).orElse(null);

        argumentsValidation.validateArguments(actionItem == null, "invalid action item %s", id);

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createdById = actionItem.getCreatedbyid();

        CheckIn checkinRecord = checkInServices.read(checkinId);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

        argumentsValidation.validateArguments(checkinId == null || createdById == null, "Invalid action item %s", actionItem);
        argumentsValidation.validateArguments(id == null || actionItemRepo.findById(id).isEmpty(), "Unable to locate action item to delete with id %s", actionItem.getId());
        argumentsValidation.validateArguments(checkInServices.read(checkinId) == null, "CheckIn %s doesn't exist", checkinId);
        argumentsValidation.validateArguments(memberServices.getById(createdById) == null, "Member %s doesn't exist", createdById);

        if (!isAdmin && isCompleted) {
            permissionsValidation.validatePermissions(true, "User is unauthorized to do this operation");
        } else if (!isAdmin && !isCompleted) {
            permissionsValidation.validatePermissions(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createdById), "User is unauthorized to do this operation");
        }

        actionItemRepo.deleteById(id);

    }

}


