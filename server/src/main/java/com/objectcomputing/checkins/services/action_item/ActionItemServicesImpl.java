package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

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
            final UUID guildId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();
            double lastDisplayOrder = 0;
            try {
                lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse(Double.valueOf(0));
            } catch (NullPointerException npe) {
                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
                //nothing needs to happen here.
            }
            actionItem.setPriority(lastDisplayOrder+1);
            if (guildId == null || createById == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (actionItem.getId() != null) {
                throw new ActionItemBadArgException(String.format("Found unexpected id %s for action item", actionItem.getId()));
            } else if (!checkinRepo.findById(guildId).isPresent()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", guildId));
            } else if (!memberRepo.findById(createById).isPresent()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
            }

            actionItemRet = actionItemRepo.save(actionItem);
        }
        return actionItemRet;
    }

    public ActionItem read(@NotNull UUID id) {
        return actionItemRepo.findById(id).orElse(null);

    }

    public Set<ActionItem> readAll() {
        Set<ActionItem> actionItems = new HashSet<>();
        actionItemRepo.findAll().forEach(actionItems::add);
        return actionItems;
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

        return new LinkedHashSet<>(
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid)));
    }

    public void delete(@NotNull UUID id) {
        actionItemRepo.deleteById(id);
    }
}
