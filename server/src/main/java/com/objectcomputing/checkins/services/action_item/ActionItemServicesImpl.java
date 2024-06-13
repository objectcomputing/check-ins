package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.validate.crud.CRUDValidator;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
class ActionItemServicesImpl implements ActionItemServices {

    private final ActionItemRepository actionItemRepo;
    private final CRUDValidator<ActionItem> crudValidator;

    ActionItemServicesImpl(ActionItemRepository actionItemRepo,
                           @Named("ActionItem") CRUDValidator<ActionItem> crudValidator) {
        this.actionItemRepo = actionItemRepo;
        this.crudValidator = crudValidator;
    }

    public ActionItem save(@Valid @NotNull ActionItem actionItem) {
        ActionItem actionItemRet;

        crudValidator.validateArgumentsCreate(actionItem);
        crudValidator.validatePermissionsCreate(actionItem);

        double lastDisplayOrder = 0.0;
        try {
            lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse(0.0);
        } catch (NullPointerException npe) {
            //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
            //nothing needs to happen here.
        }
        actionItem.setPriority(lastDisplayOrder + 1);

        actionItemRet = actionItemRepo.save(actionItem);

        return actionItemRet;
    }

    public ActionItem read(@NotNull UUID id) {
        ActionItem actionItemResult = actionItemRepo.findById(id).orElse(null);

        crudValidator.validateArgumentsRead(actionItemResult);
        if (actionItemResult != null) crudValidator.validatePermissionsRead(actionItemResult);

        return actionItemResult;
    }

    public ActionItem update(@Valid @NotNull ActionItem actionItem) {
        ActionItem actionItemRet = null;

        crudValidator.validateArgumentsUpdate(actionItem);
        crudValidator.validatePermissionsUpdate(actionItem);

        actionItemRet = actionItemRepo.update(actionItem);

        return actionItemRet;

    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {
        crudValidator.validatePermissionsFindByFields(checkinid, createdbyid);

        return new LinkedHashSet<>(
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid))
        );
    }

    public void delete(@NotNull UUID id) {
        ActionItem actionItemResult = actionItemRepo.findById(id).orElse(null);

        crudValidator.validateArgumentsDelete(actionItemResult);
        crudValidator.validatePermissionsDelete(actionItemResult);

        actionItemRepo.deleteById(id);
    }
}


