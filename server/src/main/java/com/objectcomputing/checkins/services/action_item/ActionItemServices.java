package com.objectcomputing.checkins.services.action_item;

import nu.studer.sample.tables.pojos.ActionItems;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

public interface ActionItemServices {

    ActionItems save(ActionItemCreateDTO actionItem);

    ActionItems read(UUID id);

    Set<ActionItems> readAll();

    ActionItems update(@Valid ActionItemUpdateDTO actionItem);

    Set<ActionItems> findByFields(UUID checkinid, UUID createdbyid);

    void delete(UUID id);
}
