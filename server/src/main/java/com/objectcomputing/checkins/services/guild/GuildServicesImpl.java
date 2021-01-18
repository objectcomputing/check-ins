package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

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

    public GuildServicesImpl(GuildRepository guildsRepo, CurrentUserServices currentUserServices) {
        this.guildsRepo = guildsRepo;
        this.currentUserServices = currentUserServices;
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
        if (guild.getId() != null && guildsRepo.findById(guild.getId()).isPresent()) {
            newGuild = guildsRepo.update(guild);
        } else {
            throw new BadArgException(String.format("Guild %s does not exist, can't update.", guild.getId()));
        }

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
