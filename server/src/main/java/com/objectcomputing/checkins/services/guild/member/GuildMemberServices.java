package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.guild.GuildBadArgException;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GuildMemberServices {

    @Inject
    private GuildRepository guildRepo;
    @Inject
    private GuildMemberRepository guildMemberRepo;
    @Inject
    private MemberProfileRepository memberRepo;

    protected GuildMember save(GuildMember guildMember) {
        UUID guildId = guildMember != null ? guildMember.getGuildid() : null;
        UUID memberId = guildMember != null ? guildMember.getMemberid() : null;
        if (guildId == null || memberId == null) {
            throw new GuildBadArgException(String.format("Invalid guildMember %s", guildMember));
        } else if (!guildRepo.findById(guildId).isPresent()) {
            throw new GuildBadArgException(String.format("Guild %s doesn't exist", guildId));
        } else if (!memberRepo.findById(memberId).isPresent()) {
            throw new GuildBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if(guildMemberRepo.findByGuildidAndMemberid(guildMember.getGuildid(),
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
        UUID memberId = guildMember != null ? guildMember.getMemberid() : null;
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
