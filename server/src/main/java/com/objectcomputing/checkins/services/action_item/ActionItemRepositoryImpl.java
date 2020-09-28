package com.objectcomputing.checkins.services.action_item;

import nu.studer.sample.tables.pojos.ActionItems;
import org.jooq.DSLContext;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static nu.studer.sample.Tables.ACTION_ITEMS;
import static org.jooq.impl.DSL.*;

@Singleton
public class ActionItemRepositoryImpl implements ActionItemRepository {

    private final DSLContext dslContext;

    public ActionItemRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    /*"SELECT * " +
        "FROM action_items item " +
        "WHERE (:checkinId IS NULL OR item.checkinid = :checkinId) " +
        "AND (:createdById IS NULL OR item.createdbyid = :createdById) " +
        "ORDER BY priority"*/
    @Override
    @Transactional
    public List<ActionItems> search(@Nullable UUID checkinId, @Nullable UUID createdById) {
        return dslContext.select()
                .from(ACTION_ITEMS)
                .where(condition(checkinId == null).or(ACTION_ITEMS.CHECKINID.eq(nullSafeUUIDToString(checkinId))))
                .and(condition(createdById == null).or(ACTION_ITEMS.CREATEDBYID.eq(nullSafeUUIDToString(createdById))))
                .orderBy(ACTION_ITEMS.PRIORITY)
                .fetchInto(ActionItems.class);
    }

    @Override
    @Transactional
    public List<ActionItems> saveAll(@NotNull List<ActionItems> insertUs) {
        List<ActionItems> savedRecords = new ArrayList<>();

        for (ActionItems insertMe : insertUs) {
            savedRecords.add(save(insertMe));
        }
        return savedRecords;
    }

    @Override
    @Transactional
    public ActionItems save(@NotNull ActionItems actionItems) {
        return dslContext.insertInto(ACTION_ITEMS)
                .columns(ACTION_ITEMS.ID, ACTION_ITEMS.CHECKINID, ACTION_ITEMS.CREATEDBYID, ACTION_ITEMS.DESCRIPTION, ACTION_ITEMS.PRIORITY)
                .values(String.valueOf(UUID.randomUUID()), actionItems.getCheckinid(), actionItems.getCreatedbyid(), actionItems.getDescription(), actionItems.getPriority())
                .returningResult(asterisk())
                .fetchOne().into(ActionItems.class);

    }

    @Override
    @Transactional
    public Optional<Double> findMaxPriorityByCheckinid(@NotNull UUID checkinid) {
        return dslContext.select(max(ACTION_ITEMS.PRIORITY))
                .from(ACTION_ITEMS)
                .where(ACTION_ITEMS.CHECKINID.eq(String.valueOf(checkinid)))
                .fetchOptional().map(record -> ((BigDecimal)record.get(0)).doubleValue());
    }

    @Override
    @Transactional
    public Optional<ActionItems> findById(@NotNull UUID id) {
        return dslContext.select()
            .from(ACTION_ITEMS)
            .where(ACTION_ITEMS.ID.eq(String.valueOf(id)))
            .fetchOptionalInto(ActionItems.class);
    }

    @Override
    @Transactional
    public List<ActionItems> findAll() {
        return dslContext.select().from(ACTION_ITEMS).fetchInto(ActionItems.class);
    }

    @Override
    @Transactional
    public ActionItems update(@NotNull ActionItems updateMe) {
        return dslContext.update(ACTION_ITEMS)
                .set(ACTION_ITEMS.CHECKINID, updateMe.getCheckinid())
                .set(ACTION_ITEMS.CREATEDBYID, updateMe.getCreatedbyid())
                .set(ACTION_ITEMS.DESCRIPTION, updateMe.getDescription())
                .set(ACTION_ITEMS.PRIORITY, updateMe.getPriority())
                .where(ACTION_ITEMS.ID.eq(updateMe.getId()))
                .returningResult(asterisk())
                .fetchOne().into(ActionItems.class);
    }

    @Override
    @Transactional
    public void deleteById(@NotNull UUID id) {
        dslContext.delete(ACTION_ITEMS).where(ACTION_ITEMS.ID.eq(String.valueOf(id)));
    }
}
