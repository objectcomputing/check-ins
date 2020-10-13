package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {

    private final CheckInRepository checkinRepo;
    private final ActionItemRepository actionItemRepo;
    private final MemberProfileRepository memberRepo;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;

    public ActionItemServicesImpl(CheckInRepository checkinRepo, ActionItemRepository actionItemRepo,
                                  MemberProfileRepository memberRepo, SecurityService securityService,
                                  CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.actionItemRepo = actionItemRepo;
        this.memberRepo = memberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }

    public ActionItem save(ActionItem actionItem) {
        ActionItem actionItemRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        if (actionItem != null) {
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createdById = actionItem.getCreatedbyid();

            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(checkinId == null || createdById == null, "Invalid action item %s", actionItem);
            validate(actionItem.getId() != null, "Found unexpected id %s for action item", actionItem.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createdById).isEmpty(), "Member %s doesn't exist", createdById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId) || !currentUser.getId().equals(teamMemberId), "User is unauthorized to do this operation");
            }

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

        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        ActionItem actionItemResult = actionItemRepo.findById(id).orElse(null);

        validate(actionItemResult == null, "Invalid action item id %s", id);

        if (!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(actionItemResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

        return actionItemResult;

    }

    public ActionItem update(ActionItem actionItem) {
        ActionItem actionItemRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();

            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

            validate(checkinId == null || createById == null, "Invalid action item %s", actionItem);
            validate(id == null || actionItemRepo.findById(id).isEmpty(), "Unable to locate action item to update with id %s", actionItem.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(teamMemberId), "User is unauthorized to do this operation");
            }

            actionItemRet = actionItemRepo.update(actionItem);
        }
        return actionItemRet;
    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        if (checkinid != null) {
            CheckIn checkinRecord = checkinRepo.findById(checkinid).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) &&
                    !currentUser.getId().equals(teamMemberId) &&
                    !isAdmin, "User is unauthorized to do this operation");
        } else if (createdbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createdbyid).orElse(null);
            validate(!currentUser.getId().equals(memberRecord.getId()) &&
                    !isAdmin, "User is unauthorized to do this operation");
        } else {
            validate(!isAdmin, "User is unauthorized to do this operation");
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

        validate(actionItem == null, "invalid action item %s", id);

        final UUID checkinId = actionItem.getCheckinid();
        final UUID createById = actionItem.getCreatedbyid();

        CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

        validate(checkinId == null || createById == null, "Invalid action item %s", actionItem);
        validate(id == null || actionItemRepo.findById(id).isEmpty(), "Unable to locate action item to delete with id %s", actionItem.getId());
        validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
        validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);

        if (!isAdmin && isCompleted) {
            validate(true, "User is unauthorized to do this operation");
        } else if (!isAdmin && !isCompleted) {
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

        actionItemRepo.deleteById(id);

    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new ActionItemBadArgException(String.format(message, args));
        }
    }

}


