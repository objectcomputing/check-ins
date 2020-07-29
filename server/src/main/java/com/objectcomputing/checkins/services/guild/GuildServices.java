package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
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
    private GuildMemberRepository guildMemberRepo;
    @Inject
    private MemberProfileRepository memberProfileRepository;

    protected Guild save(Guild guild) {
        Guild newGuild;
        if (guild.getGuildid() != null) {
            throw new GuildBadArgException(String.format("Found unexpected guildid %s, please try updating instead",
                    guild.getGuildid()));
        } else if (guildsRepo.findByName(guild.getName()).isPresent()) {
            throw new GuildBadArgException(String.format("Guild with name %s already exists", guild.getName()));
        } else {
            newGuild = guildsRepo.save(guild);
        }

        return newGuild;
    }

    protected Guild read(UUID guildId) {
        return guildsRepo.findByGuildid(guildId);
    }

    protected Guild update(Guild guild) {
        Guild newGuild;
        if (guild.getGuildid() != null && guildsRepo.findById(guild.getGuildid()).isPresent()) {
            newGuild = guildsRepo.update(guild);
        } else {
            throw new GuildBadArgException(String.format("Guild %s does not exist, can't update.", guild.getGuildid()));
        }

        return newGuild;
    }

    protected void load(Guild[] guildsList) {
        Arrays.stream(guildsList).forEach(this::save);
    }

    public List<Guild> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        return guildsRepo.findByNameIlike(wildcard);
    }

    public Set<Guild> findByFields(String name, UUID memberid) {
        Set<Guild> guilds = new HashSet<>();
        guildsRepo.findAll().forEach(guilds::add);
        if(name != null) {
            guilds.retainAll(guildsRepo.findByNameIlike(name));
        } else if (memberid != null) {
            guilds.retainAll(guildMemberRepo.findByMemberid(memberid)
                    .stream().map(GuildMember::getGuildid).map(gid -> guildsRepo.findById(gid).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet()));
        }
        return guilds;
    }
}
