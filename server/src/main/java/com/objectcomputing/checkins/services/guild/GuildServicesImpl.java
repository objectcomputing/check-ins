package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuildServicesImpl implements GuildServices {

    @Inject
    private GuildRepository guildsRepo;
    @Inject
    private GuildMemberRepository guildMemberRepo;

    public Guild save(Guild guild) {
        Guild newGuild = null;
        if (guild != null) {
            if (guild.getId() != null) {
                throw new GuildBadArgException(String.format("Found unexpected id %s, please try updating instead",
                        guild.getId()));
            } else if (guildsRepo.findByName(guild.getName()).isPresent()) {
                throw new GuildBadArgException(String.format("Guild with name %s already exists", guild.getName()));
            } else {
                newGuild = guildsRepo.save(guild);
            }
        }

        return newGuild;
    }

    public Guild read(UUID guildId) {
        return guildId != null ? guildsRepo.findById(guildId).orElse(null) : null;
    }

    public Guild update(Guild guild) {
        Guild newGuild = null;
        if (guild != null) {
            if (guild.getId() != null && guildsRepo.findById(guild.getId()).isPresent()) {
                newGuild = guildsRepo.update(guild);
            } else {
                throw new GuildBadArgException(String.format("Guild %s does not exist, can't update.", guild.getId()));
            }
        }

        return newGuild;
    }

    public Set<Guild> findByFields(String name, UUID memberid) {
        Set<Guild> guilds = new HashSet<>();
        guildsRepo.findAll().forEach(guilds::add);
        if (name != null) {
            guilds.retainAll(guildsRepo.findByNameIlike(name));
        }
        if (memberid != null) {
            guilds.retainAll(guildMemberRepo.findByMemberid(memberid)
                    .stream().map(GuildMember::getGuildid).map(id -> guildsRepo.findById(id).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet()));
        }
        return guilds;
    }
}
