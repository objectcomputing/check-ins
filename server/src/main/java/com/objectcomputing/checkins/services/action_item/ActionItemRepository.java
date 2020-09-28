package com.objectcomputing.checkins.services.action_item;

import nu.studer.sample.tables.pojos.ActionItems;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


//@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ActionItemRepository {//extends CrudRepository<ActionItem, UUID> {

    /*@Query("SELECT * " +
            "FROM action_items item " +
            "WHERE (:checkinId IS NULL OR item.checkinid = :checkinId) " +
            "AND (:createdById IS NULL OR item.createdbyid = :createdById) " +
            "ORDER BY priority")*/
    List<ActionItems> search(@Nullable UUID checkinId, @Nullable UUID createdById);

    List<ActionItems> saveAll(@NotEmpty List<ActionItems> insertUs);

    //@Override
    ActionItems save(@NotNull ActionItems insertMe);

    Optional<Double> findMaxPriorityByCheckinid(UUID checkinid);

    Optional<ActionItems> findById(UUID id);

    List<ActionItems> findAll();

    ActionItems update(@NotNull ActionItems updateMe);

    void deleteById(UUID id);
}
