package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.guild.Guild;

import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class CRUDValidatorFactory {

    @Singleton
    public CRUDValidator<ActionItem> createActionItemCRUDValidator(ActionItemCRUDValidator validator) {
        return validator;
    }

    @Singleton
    public CRUDValidator<Guild> createGuildCRUDValidator(GuildCRUDValidator validator) {
        return validator;
    }
}
