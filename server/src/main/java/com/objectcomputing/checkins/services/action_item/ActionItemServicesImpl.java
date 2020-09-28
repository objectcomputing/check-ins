package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import nu.studer.sample.tables.pojos.ActionItems;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {

    private final CheckInRepository checkinRepo;
    private final ActionItemRepository actionItemRepo;
    private final MemberProfileRepository memberRepo;

    public ActionItemServicesImpl(CheckInRepository checkinRepo,
                                  ActionItemRepository actionItemRepo,
                                  MemberProfileRepository memberRepo) {

        this.checkinRepo = checkinRepo;
        this.actionItemRepo = actionItemRepo;
        this.memberRepo = memberRepo;
    }

    public ActionItems save(ActionItemCreateDTO actionItem) {
        ActionItems actionItemRet = null;
        if (actionItem != null) {
            final UUID guildId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();
            double lastDisplayOrder = 0;
            try {
                lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse((double) 0);
            } catch (NullPointerException npe) {
                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
                //nothing needs to happen here.
            }
            ActionItems insertMe = dtoToEntity(actionItem, lastDisplayOrder);
            if (guildId == null || createById == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            }  else if (checkinRepo.findById(guildId).isEmpty()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", guildId));
            } else if (memberRepo.findById(createById).isEmpty()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
            }

            actionItemRet = actionItemRepo.save(insertMe);
        }
        return actionItemRet;
    }

    private ActionItems dtoToEntity(ActionItemCreateDTO dto, double priority) {
        return new ActionItems(null, String.valueOf(dto.getCheckinid()), String.valueOf(dto.getCreatedbyid()), dto.getDescription(), BigDecimal.valueOf(priority));
    }

    public ActionItems read(@NotNull UUID id) {
        return actionItemRepo.findById(id).orElse(null);

    }

    public Set<ActionItems> readAll() {
        return new HashSet<>(actionItemRepo.findAll());
    }

    public ActionItems update(ActionItemUpdateDTO actionItem) {
        ActionItems actionItemRet = null;
        if (actionItem != null) {
            final UUID id = actionItem.getId();
            final UUID guildId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();
            if (guildId == null || createById == null) {
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (id == null || actionItemRepo.findById(id).isEmpty()) {
                throw new ActionItemBadArgException(String.format("Unable to locate actionItem to update with id %s", id));
            } else if (checkinRepo.findById(guildId).isEmpty()) {
                throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", guildId));
            } else if (memberRepo.findById(createById).isEmpty()) {
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
            }

            actionItemRet = actionItemRepo.update(updateDtoToEntity(actionItem));
        }
        return actionItemRet;
    }

    private ActionItems updateDtoToEntity(ActionItemUpdateDTO dto) {
        return new ActionItems(String.valueOf(dto.getId()), String.valueOf(dto.getCheckinid()),
                String.valueOf(dto.getCreatedbyid()), dto.getDescription(), BigDecimal.valueOf(dto.getPriority()));
    }

    public Set<ActionItems> findByFields(UUID checkinid, UUID createdbyid) {

        return new LinkedHashSet<>(
                actionItemRepo.search(checkinid, createdbyid));
    }

    public void delete(@NotNull UUID id) {
        actionItemRepo.deleteById(id);
    }
}


