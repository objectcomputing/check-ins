package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class CRUDValidatorFactory {

    private ArgumentsValidation argumentsValidation;
    private CheckInServices checkInServices;
    private MemberProfileServices memberServices;

    @Singleton
    CRUDValidator<ActionItem> createActionItemCRUDValidator() {
        return new ActionItemCRUDValidator(checkInServices, memberServices, argumentsValidation);
    }

//    @Singleton
//    CRUDValidator<SomeOtherEntity> createSomeOtherEntityCRUDValidator() {
//        return new SomeOtherEntityCRUDValidator();
//    }

}
