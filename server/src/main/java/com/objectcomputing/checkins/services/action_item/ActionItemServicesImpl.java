package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

public class ActionItemServicesImpl implements ActionItemServices {

    private CheckInRepository checkinRepo;
    private ActionItemRepository actionItemRepo;
    private MemberProfileRepository memberRepo;
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;

    public ActionItemServicesImpl(CheckInRepository checkinRepo, ActionItemRepository actionItemRepo,
                                  MemberProfileRepository memberRepo, SecurityService securityService,
                                  CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.actionItemRepo = actionItemRepo;
        this.memberRepo = memberRepo;

    }

    public ActionItem save(ActionItem actionItem) {
        ActionItem actionItemRet = null;
        if (actionItem != null) {
            final UUID checkinid = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();
            if (checkinid == null || createById == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (actionItem.getId() != null) {
                throw new ActionItemBadArgException(String.format("Found unexpected id %s for action item", actionItem.getId()));
            } else if (checkinRepo.findById(checkinid).isEmpty()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", checkinid));
            } else if (memberRepo.findById(createById).isEmpty()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
            }

            actionItemRet = actionItemRepo.save(actionItem);
        }
        return actionItemRet;
    }

    public ActionItem read(@NotNull UUID id) {
        return actionItemRepo.findById(id).orElse(null);

    }
//
//    public Set<ActionItem> readAll() {
//        Set<ActionItem> actionItems = new HashSet<>();
//        actionItemRepo.findAll().forEach(actionItems::add);
//        return actionItems;
//    }

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
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

/*        Limit read
        subject of the check in
        the current PDL of the subject of the check in
        the admin */

        if(isAdmin || currentUser.getUuid().equals(teamMemberId) || currentUser.getUuid().equals(pdlId) ||
                currentUser.getUuid().equals(memberRepo.findById(teamMemberId).get().getPdlId())) {

            Set<ActionItem> actionItems = new HashSet<>(
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid)));

            return actionItems;
        }
        throw new ActionItemBadArgException(String.format("Member %s is unauthorized to do this operation", currentUser.getUuid()));
    }

    public void delete(@NotNull UUID id) {
        actionItemRepo.deleteById(id);
    }
}


