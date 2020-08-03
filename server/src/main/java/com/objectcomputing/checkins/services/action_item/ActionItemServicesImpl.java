package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ActionItemServicesImpl implements ActionItemServices {

    @Inject
    private CheckInRepository checkinRepo;
    @Inject
    private ActionItemRepository actionItemRepo;
    @Inject
    private MemberProfileRepository memberRepo;

    public ActionItem save(ActionItem actionItem) {
        ActionItem actionItemRet = null;
        if (actionItem != null) {
            final UUID checkinId = actionItem.getCheckinid();
            final UUID memberId = actionItem.getCreatedbyid();
            if (checkinId == null || memberId == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (actionItem.getId() != null) {
                throw new ActionItemBadArgException(String.format("Found unexpected id %s for action item", actionItem.getId()));
            } else if (!checkinRepo.findById(checkinId).isPresent()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", memberId));
            }

            actionItemRet = actionItemRepo.save(actionItem);
        }
        return actionItemRet;
    }

    public ActionItem read(@NotNull UUID id) {
        return actionItemRepo.findById(id).orElse(null);
    }

    public ActionItem update(ActionItem actionItem) {
        ActionItem actionItemRet = null;
        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID checkinId = actionItem.getCheckinid();
            final UUID memberId = actionItem.getCreatedbyid();
            if (checkinId == null || memberId == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (id == null || !actionItemRepo.findById(id).isPresent()) {
                throw new ActionItemBadArgException(String.format("Unable to locate actionItem to update with id %s", id));
            } else if (!checkinRepo.findById(checkinId).isPresent()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", memberId));
            }

            actionItemRet = actionItemRepo.update(actionItem);
        }
        return actionItemRet;
    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {
        Set<ActionItem> actionItems = new HashSet<>();
        actionItemRepo.findAll().forEach(actionItems::add);

        if (checkinid != null) {
            actionItems.retainAll(actionItemRepo.findByCheckinid(checkinid));
        }
        if (createdbyid != null) {
            actionItems.retainAll(actionItemRepo.findByCreatedbyid(createdbyid));
        }

        return actionItems;
    }
}
