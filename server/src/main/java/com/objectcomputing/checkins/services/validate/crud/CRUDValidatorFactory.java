package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class CRUDValidatorFactory {

    @Singleton
    public CRUDValidator<ActionItem> createActionItemCRUDValidator(ActionItemCRUDValidator validator) {
        return validator;
    }

//    @Singleton
//    CRUDValidator<SomeOtherEntity> createSomeOtherEntityCRUDValidator() {
//        return new SomeOtherEntityCRUDValidator();
//    }

}
