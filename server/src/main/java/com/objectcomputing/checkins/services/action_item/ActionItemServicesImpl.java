package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {
    private static final Logger LOG = LoggerFactory.getLogger(ActionItemServicesImpl.class);

    private CheckInRepository checkinRepo;
    private ActionItemRepository actionItemRepo;
    private MemberProfileRepository memberRepo;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public ActionItemServicesImpl(CheckInRepository checkinRepo,
                                  ActionItemRepository actionItemRepo,
                                  MemberProfileRepository memberRepo, EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService) {
        this.checkinRepo = checkinRepo;
        this.actionItemRepo = actionItemRepo;
        this.memberRepo = memberRepo;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    public Single<ActionItem> save(ActionItem actionItem) {
        LOG.info("Entering service on main event loop.");
        return Single.fromCallable(() -> {
            LOG.info("Processing Single on I/O loop");
            ActionItem actionItemRet = null;
            if (actionItem != null) {
                final UUID guildId = actionItem.getCheckinid();
                final UUID createById = actionItem.getCreatedbyid();
                LOG.info("Making multiple calls within this I/O thread as thread switching is costly.");
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
        })
        .subscribeOn(Schedulers.from(ioExecutorService));
    }

    public Single<ActionItem> read(@NotNull UUID id) {
        return Single.fromCallable(() ->
                actionItemRepo.findById(id).orElse(null))
                .subscribeOn(Schedulers.from(ioExecutorService));

    }

    public Single<Set<ActionItem>> readAll() {
        LOG.info("Entering service on main loop");
        return Single.fromCallable(() -> {
                    LOG.info("Finding the records on the I/O loop");
                    return actionItemRepo.findAll();
                })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(actionItems -> {
                    LOG.info("transforming the iterable from the repo into the expected set");
                    Set<ActionItem> actionItemSet = new HashSet<>();
                    actionItems.forEach(actionItemSet::add);
                    return actionItemSet;
                })
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    public Single<ActionItem> update(ActionItem actionItem) {
        LOG.info("Entering service on main loop");
        return Single.fromCallable(() -> {
            LOG.info("Updating on main event loop");
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

                return actionItemRepo.update(actionItem);
            }
            return null;
        }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    public Single<Set<ActionItem>> findByFields(UUID checkinid, UUID createdbyid) {
        return Single.fromCallable(() ->
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid)))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    public void delete(@NotNull UUID id) {
        Completable.fromAction(() -> actionItemRepo.deleteById(id)).subscribeOn(Schedulers.from(ioExecutorService));
    }
}


