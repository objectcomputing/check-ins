package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import com.objectcomputing.checkins.services.validate.crud.CRUDValidator;
import com.objectcomputing.checkins.services.validate.crud.CRUDValidatorFactory;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {

    private final ActionItemRepository actionItemRepo;
    private final CRUDValidator<ActionItem> crudValidator;
    private final ArgumentsValidation argumentsValidation;
    private final PermissionsValidation permissionsValidation;

    public ActionItemServicesImpl(ActionItemRepository actionItemRepo,
                                  CRUDValidator<ActionItem> crudValidator, ArgumentsValidation argumentsValidation,
                                  PermissionsValidation permissionsValidation) {
        this.actionItemRepo = actionItemRepo;
        this.crudValidator = crudValidator;
        this.argumentsValidation = argumentsValidation;
        this.permissionsValidation = permissionsValidation;
    }

    public ActionItem save(@Valid @NotNull ActionItem actionItem) {
        ActionItem actionItemRet = null;

//        argumentsValidation.validateActionItemArgumentsForSave(actionItem);
        crudValidator.validateCreate(actionItem);
        permissionsValidation.validateActionItemPermissions(actionItem);

        double lastDisplayOrder = 0;
        try {
            lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse(Double.valueOf(0));
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

        argumentsValidation.validateActionItemArgumentsForRead(actionItemResult, id);
        if (actionItemResult != null) permissionsValidation.validateActionItemPermissionsForRead(actionItemResult);

        return actionItemResult;

    }

    public ActionItem update(@Valid @NotNull ActionItem actionItem) {
        ActionItem actionItemRet = null;

        argumentsValidation.validateActionItemArgumentsForUpdate(actionItem);
        permissionsValidation.validateActionItemPermissionsForUpdate(actionItem);

        actionItemRet = actionItemRepo.update(actionItem);

        return actionItemRet;

    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {

        permissionsValidation.validateActionItemPermissionsForFindByFields(checkinid, createdbyid);

        Set<ActionItem> actionItems = new LinkedHashSet<>(
                actionItemRepo.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createdbyid)));

        return actionItems;

    }

    public void delete(@NotNull UUID id) {

        argumentsValidation.validateActionItemArgumentsForDelete(id);
        permissionsValidation.validateActionItemPermissionsForDelete(id);

        actionItemRepo.deleteById(id);

    }

}


