package com.objectcomputing.checkins.services.action_item;

import io.reactivex.Single;

import java.util.Set;
import java.util.UUID;

public interface ActionItemServices {

    Single<ActionItem> save(ActionItem actionItem);

    Single<ActionItem> read(UUID id);

    Single<Set<ActionItem>> readAll();

    Single<ActionItem> update(ActionItem actionItem);

    Single<Set<ActionItem>> findByFields(UUID checkinid, UUID createdbyid);

    void delete(UUID id);
}
