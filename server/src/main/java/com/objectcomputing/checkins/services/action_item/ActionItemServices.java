package com.objectcomputing.checkins.services.action_item;

import java.util.Set;
import java.util.UUID;

public interface ActionItemServices {

    ActionItem save(ActionItem actionItem);

    ActionItem read(UUID id);

    ActionItem update(ActionItem actionItem);

    Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid);

    void delete(UUID id);
}
