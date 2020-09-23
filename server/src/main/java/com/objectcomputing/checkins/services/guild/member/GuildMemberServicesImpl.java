package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.guild.GuildBadArgException;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class GuildMemberServicesImpl implements GuildMemberServices {

    private GuildRepository guildRepo;

    private GuildMemberRepository guildMemberRepo;

    private MemberProfileRepository memberRepo;

    public GuildMemberServicesImpl(GuildRepository guildRepo, GuildMemberRepository guildMemberRepo, MemberProfileRepository memberRepo) {
        this.guildRepo = guildRepo;
        this.guildMemberRepo = guildMemberRepo;
        this.memberRepo = memberRepo;
    }

    public GuildMember save(GuildMember guildMember) {
        GuildMember guildMemberRet = null;
        if (guildMember != null) {
            final UUID guildId = guildMember.getGuildid();
            final UUID memberId = guildMember.getMemberid();

            if (guildId == null || memberId == null) {
                throw new GuildBadArgException(String.format("Invalid guildMember %s", guildMember));
            } else if (guildMember.getId() != null) {
                throw new GuildBadArgException(String.format("Found unexpected id %s for guild member", guildMember.getId()));
            } else if (guildRepo.findById(guildId).isEmpty()) {
                throw new GuildBadArgException(String.format("Guild %s doesn't exist", guildId));
            } else if (memberRepo.findById(memberId).isEmpty()) {
                throw new GuildBadArgException(String.format("Member %s doesn't exist", memberId));
            } else if (!guildMemberRepo.search(guildMember.getGuildid().toString(),
                    guildMember.getMemberid().toString(), guildMember.isLead()).isEmpty()) {
                throw new GuildBadArgException(String.format("Member %s already exists in guild %s", memberId, guildId));
            }

            guildMemberRet = guildMemberRepo.save(guildMember);
        }
        return guildMemberRet;
    }

    public GuildMember read(@NotNull UUID id) {
        return guildMemberRepo.findById(id).orElse(null);
    }

    public GuildMember update(GuildMember guildMember) {
        GuildMember guildMemberRet = null;
        if (guildMember != null) {
            final UUID id = guildMember.getId();
            final UUID guildId = guildMember.getGuildid();
            final UUID memberId = guildMember.getMemberid();
            if (guildId == null || memberId == null) {
                throw new GuildBadArgException(String.format("Invalid guildMember %s", guildMember));
            } else if (id == null || !guildMemberRepo.findById(id).isPresent()) {
                throw new GuildBadArgException(String.format("Unable to locate guildMember to update with id %s", id));
            } else if (!guildRepo.findById(guildId).isPresent()) {
                throw new GuildBadArgException(String.format("Guild %s doesn't exist", guildId));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new GuildBadArgException(String.format("Member %s doesn't exist", memberId));
            }

            guildMemberRet = guildMemberRepo.update(guildMember);
        }
        return guildMemberRet;
    }

    public Set<GuildMember> findByFields(UUID guildid, UUID memberid, Boolean lead) {
        Set<GuildMember> guildMembers = new HashSet<>(
                guildMemberRepo.search(nullSafeUUIDToString(guildid), nullSafeUUIDToString(memberid), lead));

        return guildMembers;
    }

}
