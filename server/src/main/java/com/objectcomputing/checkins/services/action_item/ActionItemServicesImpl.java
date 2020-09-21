package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.reactivex.exceptions.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {
    private static final Logger LOG = LoggerFactory.getLogger(ActionItemServicesImpl.class);

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

    public ActionItem save(ActionItem actionItem) {
        LOG.info("Processing Single on I/O loop");
        ActionItem actionItemRet = null;
        if (actionItem != null) {
            final UUID checkinId = actionItem.getCheckinid();
            final UUID createById = actionItem.getCreatedbyid();
            LOG.info("Making multiple calls within this I/O thread as thread switching is costly.");
            if (checkinId == null || createById == null) {
                LOG.info("INVALID");
                throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            } else if (actionItem.getId() != null) {
                LOG.info("NO ID");
                throw new ActionItemBadArgException(String.format("Found unexpected id %s for action item", actionItem.getId()));
            } else if (checkinRepo.findById(checkinId).isEmpty()) {
                LOG.info("NO CHECKIN");
                //throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
                throw Exceptions.propagate(new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", checkinId)));
            } else if (memberRepo.findById(createById).isEmpty()) {
                LOG.info("NO MEMBER");
                throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
            }
            actionItemRet = actionItemRepo.save(actionItem);
        }
        return actionItemRet;
    }

    public ActionItem read(@NotNull UUID id) {
        /*Optional<ActionItem> found = actionItemRepo.findById(id);
        if (found.isPresent()) {
            return found.get();
        }
        throw new ActionItemNotFoundException("No action item for UUID");*/
        return actionItemRepo.findById(id).orElse(null);
    }

    public Set<ActionItem> readAll() {
        LOG.info("Entering service on I/O loop");
        Set<ActionItem> actionItemSet = new HashSet<>();
        actionItemRepo.findAll().forEach(actionItemSet::add);
        return actionItemSet;
    }

    public ActionItem update(ActionItem actionItem) {
        LOG.info("Updating on I/O loop");
        final UUID id = actionItem.getId();
        final UUID checkinId = actionItem.getCheckinid();
        final UUID createById = actionItem.getCreatedbyid();
        if (checkinId == null || createById == null) {
            //throw new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem));
            Exceptions.propagate(new ActionItemBadArgException(String.format("Invalid actionItem %s", actionItem)));
        } else if (id == null || actionItemRepo.findById(id).isEmpty()) {
            throw new ActionItemBadArgException(String.format("Unable to locate actionItem to update with id %s", id));
        } else if (checkinRepo.findById(checkinId).isEmpty()) {
            throw new ActionItemBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
        } else if (memberRepo.findById(createById).isEmpty()) {
            throw new ActionItemBadArgException(String.format("Member %s doesn't exist", createById));
        }
        return actionItemRepo.update(actionItem);
    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {
        return actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid));
        /*return Single.fromCallable(() ->
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid)))
                .subscribeOn(Schedulers.from(ioExecutorService));*/
    }

    public void delete(@NotNull UUID id) {
            LOG.info("Nothing is being returned, but the I/O should still be done in an Observable");
            actionItemRepo.deleteById(id);
    }
}


