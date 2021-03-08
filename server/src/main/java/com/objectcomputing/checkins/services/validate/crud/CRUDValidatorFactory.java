package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.guild.Guild;

import io.micronaut.context.annotation.Factory;

import javax.inject.Named;
import javax.inject.Singleton;

@Factory
public class CRUDValidatorFactory {

    @Singleton
    @Named("ActionItem")
    public CRUDValidator<ActionItem> createActionItemCRUDValidator(ActionItemCRUDValidator validator) {
        return validator;
    }

    @Singleton
    @Named("Guild")
    public CRUDValidator<Guild> createGuildCRUDValidator(GuildCRUDValidator validator) {
        return validator;
    }
}
