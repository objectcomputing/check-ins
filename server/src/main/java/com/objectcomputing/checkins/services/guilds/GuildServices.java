package com.objectcomputing.checkins.services.guilds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class GuildServices {

    private static final Logger LOG = LoggerFactory.getLogger(GuildServices.class);

    @Inject
    private GuildRepository guildsRepo;
    @Inject
    private GuildMemberServices guildMemberServices;

    protected Guild save(Guild guild) {
        Guild oldGuild = guildsRepo.findByName(guild.getName());
        Guild newGuild = null;
        if(oldGuild == null) {
            newGuild = guildsRepo.save(guild);
            final UUID guildId = newGuild.getGuildId();
            if(guild.getMembers() != null) {
                for (GuildMember gm : guild.getMembers()) {
                    gm.setGuildId(guildId);
                    GuildMember newMember = guildMemberServices.save(gm);
                    if(newMember == null) {
                        LOG.error(String.format("Unable to save guild member %s to guild: %s", gm.getMemberId(), guildId));
                    }
                }
                newGuild = guildsRepo.findById(guildId).orElse(null);
            }
        } else {
            LOG.error(String.format("Unable to create guild, as a guild by this name already exists, %s",
                    guild.getName()));
        }
        return newGuild;
    }

    protected Guild read(UUID guildId) {
        return guildsRepo.findByGuildId(guildId);
    }

    protected Guild update(Guild guild) {
        Guild newGuild = guildsRepo.update(guild);
        final UUID guildId = newGuild.getGuildId();
        if(guild.getMembers() != null) {
            // Figure out old guild members
            Map<UUID, Boolean> oldMembers = guildMemberServices.findByGuildId(newGuild.getGuildId()).stream()
                    .collect(Collectors.toMap(GuildMember::getGuildId, gm -> true));

            for (GuildMember gm : guild.getMembers()) {
                gm.setGuildId(guildId);
                GuildMember newMember = guildMemberServices.update(gm);
                if(newMember == null) {
                    LOG.error(String.format("Unable to update guild member %s", gm.getMemberId()));
                }

                // Mark guild members that are updated as not to be removed
                oldMembers.put(gm.getMemberId(), false);
            }

            // Remove all guild members that have been removed
            oldMembers.entrySet().stream().filter(Map.Entry::getValue)
                    .forEach(e -> guildMemberServices.deleteById(guildId, e.getKey()));

            newGuild = guildsRepo.findById(guildId).orElse(null);
        }

        return newGuild;
    }

    protected void load(Guild[] guildsList)
    {
        Arrays.stream(guildsList).forEach(this::save);
    }

    protected List<Guild> findByIdOrLikeName(UUID guildid, String name) {
        List<Guild> guild = null;
        if (guildid != null) {
            guild = Collections.singletonList(read(guildid));
        } else if(name != null) {
            guild = findByNameLike(name);
        }

        return guild;
    }

    private List<Guild> findByNameLike(String name) {
        String wildcard = "%" + name + "%" ;
        return guildsRepo.findByNameIlike(wildcard);
    }
}
