package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkin_notes.CheckinNote;
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
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;

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
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (actionItem != null) {
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();

            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid checkin note %s", actionItem);
            validate(actionItem.getId() != null, "Found unexpected id %s for action item", actionItem.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(!currentUser.getId().equals(pdlId), "User is unauthorized to do this operation");
            }

            double lastDisplayOrder = 0;
            try {
                lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse(Double.valueOf(0));
            } catch (NullPointerException npe) {
                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
                //nothing needs to happen here.
            }
            actionItem.setPriority(lastDisplayOrder+1);

            actionItemRet = actionItemRepo.save(actionItem);
        }

//        ActionItem actionItemRet = null;
//        if (actionItem != null) {
//            final UUID actionItemId = actionItem.getCheckinid();
//            final UUID createById = actionItem.getCreatedbyid();
//            double lastDisplayOrder = 0;
//            try {
//                lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse(Double.valueOf(0));
//            } catch (NullPointerException npe) {
//                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
//                //nothing needs to happen here.
//            }
//            actionItem.setPriority(lastDisplayOrder+1);
//            if (actionItemId == null || createById == null) {
//                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
//            } else if (actionItem.getId() != null) {
//                throw new ActionItemBadArgException(String.format("Found unexpected id %s for action item", actionItem.getId()));
//            } else if (checkinRepo.findById(actionItemId).isEmpty()) {
//                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", actionItemId));
//            } else if (memberRepo.findById(createById).isEmpty()) {
//                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
//            }
//
//            actionItemRet = actionItemRepo.save(actionItem);
//        }
        return actionItemRet;
    }

    public ActionItem read(@NotNull UUID id) {

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        ActionItem actionItemResult = actionItemRepo.findById(id).orElse(null);

        validate(actionItemResult == null, "Invalid action item id %s", id);

        if(!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(actionItemResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord!=null?checkinRecord.getPdlId():null;
            final UUID createById = checkinRecord!=null?checkinRecord.getTeamMemberId():null;
            validate(!currentUser.getId().equals(pdlId)&&!currentUser.getId().equals(createById),"User is unauthorized to do this operation");
        }

        return actionItemResult;

    }

    public ActionItem update(ActionItem actionItem) {
        ActionItem actionItemRet = null;
        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID guildId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();
            if (guildId == null || createById == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (id == null || !actionItemRepo.findById(id).isPresent()) {
                throw new ActionItemBadArgException(String.format("Unable to locate actionItem to update with id %s", id));
            } else if (!checkinRepo.findById(guildId).isPresent()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", guildId));
            } else if (!memberRepo.findById(createById).isPresent()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
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
        actionItemRepo.deleteById(id);
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new ActionItemBadArgException(String.format(message, args));
        }
    }

}


