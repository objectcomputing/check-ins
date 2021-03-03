package com.objectcomputing.checkins.services.validate.crud;


import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class GuildCRUDValidator implements CRUDValidator<Guild> {
    private final ArgumentsValidation argumentsValidation;
    private final PermissionsValidation permissionsValidation;
    private final CurrentUserServices currentUserServices;
    private final GuildRepository guildsRepo;



    @Inject
    public GuildCRUDValidator(GuildRepository guildsRepo,ArgumentsValidation argumentsValidation, PermissionsValidation permissionsValidation,
                                   CurrentUserServices currentUserServices) {
        this.argumentsValidation = argumentsValidation;
        this.permissionsValidation = permissionsValidation;
        this.currentUserServices = currentUserServices;
        this.guildsRepo = guildsRepo;
    }


    @Override
    public void validateArgumentsCreate(Guild guild) {

    }

    @Override
    public void validateArgumentsRead(Guild guild) {

    }

    @Override
    public void validateArgumentsUpdate(@Valid @NotNull Guild guild) {
        if(guild!=null) {
            final UUID id = guild.getId();
            argumentsValidation.validateArguments(id == null || guildsRepo.findById(id).isEmpty(), "Unable to locate guild to update with id %s", guild.getId());
        }
    }

    @Override
    public void validateArgumentsDelete(Guild guild) {

    }

    @Override
    public void validatePermissionsCreate(Guild guild) {

    }

    @Override
    public void validatePermissionsRead(Guild guild) {

    }

    @Override
    public void validatePermissionsUpdate(Guild guild) {
        boolean isAdmin = currentUserServices.isAdmin();
        if(!isAdmin) {
            permissionsValidation.validatePermissions(true, "User is unauthorized to do this operation");
        }
    }

    @Override
    public void validatePermissionsFindByFields(UUID entity, UUID secondEntity) {

    }

    @Override
    public void validatePermissionsDelete(Guild entity) {

    }
}
