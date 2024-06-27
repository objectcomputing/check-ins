package com.objectcomputing.checkins.services.validate.crud;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.ArgumentsValidation;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Named("Guild")
public class GuildCRUDValidator implements CRUDValidator<Guild> {

    private final ArgumentsValidation argumentsValidation;
    private final PermissionsValidation permissionsValidation;
    private final CurrentUserServices currentUserServices;
    private final GuildRepository guildsRepo;

    @Inject
    public GuildCRUDValidator(GuildRepository guildsRepo,
                              ArgumentsValidation argumentsValidation,
                              PermissionsValidation permissionsValidation,
                              CurrentUserServices currentUserServices) {
        this.argumentsValidation = argumentsValidation;
        this.permissionsValidation = permissionsValidation;
        this.currentUserServices = currentUserServices;
        this.guildsRepo = guildsRepo;
    }

    @Override
    public void validateArgumentsCreate(@NotNull Guild guild) {
        if (guild != null) {
            final UUID id = guild.getId();
            final String name = guild.getName();

            argumentsValidation.validateArguments(id != null,
                    "Found unexpected id %s, please try updating instead", id);
            argumentsValidation.validateArguments(guildsRepo.findByName(name).isPresent(),
                    "Guild with name %s already exists", name);
        }
    }

    @Override
    public void validateArgumentsRead(@NotNull Guild guild) {
        // Currently not used.
    }

    @Override
    public void validateArgumentsUpdate(@NotNull Guild guild) {
        if (guild != null) {
            final UUID id = guild.getId();
            argumentsValidation.validateArguments(id == null || guildsRepo.findById(id).isEmpty(),
                    "Unable to locate guild to update with ID %s", id);
        }
    }

    @Override
    public void validateArgumentsDelete(Guild guild) {
        argumentsValidation.validateArguments(guild == null, "Not found the guild to delete");
    }

    @Override
    public void validatePermissionsCreate(Guild guild) {
        validatePermissionCommon();
    }

    @Override
    public void validatePermissionsRead(Guild guild) {
        // Currently not used; anyone can read.
    }

    @Override
    public void validatePermissionsUpdate(Guild guild) {
        validatePermissionCommon();
    }

    @Override
    public void validatePermissionsFindByFields(UUID entity, UUID secondEntity) {
        // Currently not used; anyone can find by fields.
    }

    @Override
    public void validatePermissionsDelete(Guild entity) {
        validatePermissionCommon();
    }

    private void validatePermissionCommon() {
        permissionsValidation.validatePermissions(!currentUserServices.isAdmin());
    }
}
