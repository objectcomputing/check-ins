package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.crud.CRUDValidator;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class GuildServicesImpl implements GuildServices {

    private final GuildRepository guildsRepo;
    private final CurrentUserServices currentUserServices;
    private final CRUDValidator<Guild> crudValidator;


    public GuildServicesImpl(GuildRepository guildsRepo, CurrentUserServices currentUserServices,CRUDValidator<Guild> crudValidator) {
        this.guildsRepo = guildsRepo;
        this.currentUserServices = currentUserServices;
        this.crudValidator = crudValidator;
    }

    public Guild save(@NotNull Guild guild) {
        Guild newGuild = null;

        if (guild.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s, please try updating instead",
                    guild.getId()));
        } else if (guildsRepo.findByName(guild.getName()).isPresent()) {
            throw new BadArgException(String.format("Guild with name %s already exists", guild.getName()));
        } else {
            newGuild = guildsRepo.save(guild);
        }

        return newGuild;
    }

    public Guild read(@NotNull UUID guildId) {
        return guildsRepo.findById(guildId).orElse(null);
    }

    public Guild update(@NotNull Guild guild) {
        Guild newGuild = null;
        crudValidator.validateArgumentsUpdate(guild);
        crudValidator.validatePermissionsUpdate(guild);

        newGuild = guildsRepo.update(guild);

        return newGuild;
    }

    public Set<Guild> findByFields(String name, UUID memberid) {
        String likeName = null;
        if (name != null) {
            likeName = "%" + name + "%";
        }
        Set<Guild> guilds = new HashSet<>(
                guildsRepo.search(likeName, nullSafeUUIDToString(memberid)));

        return guilds;

    }

    public Boolean delete(@NotNull UUID id) {

        Guild guildResult = guildsRepo.findById(id).orElse(null);

        if (guildResult == null) {
            throw new NotFoundException(String.format("No guild for id %s", id));
        }
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to perform this operation");
        }

        guildsRepo.deleteById(id);
        return true;
    }
}
