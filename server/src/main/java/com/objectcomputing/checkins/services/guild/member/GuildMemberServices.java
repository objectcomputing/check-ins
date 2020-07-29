package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.guild.GuildBadArgException;

import javax.inject.Inject;
import java.util.*;

public class GuildMemberServices {

    @Inject
    private GuildMemberRepository guildMemberRepo;

    protected GuildMember save(GuildMember guildMember) {
        UUID guildId = guildMember != null ? guildMember.getGuildid() : null;
        UUID memberId = guildMember != null ? guildMember.getGuildid() : null;
        if (guildId == null || memberId == null) {
            throw new GuildBadArgException(String.format("Invalid guildMember %s", guildMember));
        } else if (guildMemberRepo.findByGuildidAndMemberid(guildMember.getGuildid(),
                guildMember.getMemberid()).isPresent()) {
            throw new GuildBadArgException(String.format("Member %s already exists in guild %s", guildId, memberId));
        }

        return guildMemberRepo.save(guildMember);
    }

    protected GuildMember read(UUID id) {
        return guildMemberRepo.findById(id).orElse(null);
    }

    protected GuildMember update(GuildMember guildMember) {
        UUID guildId = guildMember != null ? guildMember.getGuildid() : null;
        UUID memberId = guildMember != null ? guildMember.getGuildid() : null;
        if (guildId == null || memberId == null) {
            throw new GuildBadArgException(String.format("Invalid guildMember %s", guildMember));
        } else if (!guildMemberRepo.findByGuildidAndMemberid(guildMember.getGuildid(),
                guildMember.getMemberid()).isPresent()) {
            throw new GuildBadArgException(String.format("Member %s does not already exists in guild %s", guildId, memberId));
        }
        return guildMemberRepo.update(guildMember);
    }

    protected void load(GuildMember[] guildMemberlist) {
        Arrays.stream(guildMemberlist).forEach(this::save);
    }

    public void deleteById(UUID guildId, UUID memberId) {
        guildMemberRepo.findByGuildidAndMemberid(guildId, memberId)
                .ifPresent(gm -> guildMemberRepo.deleteById(gm.getId()));
    }

    public Set<GuildMember> findByFields(UUID guildid, UUID memberid, Boolean lead) {
        Set<GuildMember> guildMembers = new HashSet<>();
        guildMemberRepo.findAll().forEach(guildMembers::add);

        if (guildid != null) {
            guildMembers.retainAll(guildMemberRepo.findByGuildid(guildid));
        }
        if (memberid != null) {
            guildMembers.retainAll(guildMemberRepo.findByMemberid(memberid));
        }
        if (lead != null) {
            guildMembers.retainAll(guildMemberRepo.findByLead(lead));
        }

        return guildMembers;
    }
}
