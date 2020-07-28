package com.objectcomputing.checkins.services.guilds;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GuildMemberServices {

    @Inject
    private GuildMemberRepository guildMemberRepo;

    protected GuildMember save(GuildMember guildMember) {
        return guildMemberRepo.save(guildMember);
    }

    protected GuildMember read(UUID guildMemberId, UUID memberId) {
        return guildMemberRepo.findById(new GuildMemberCompositeKey(guildMemberId, memberId)).orElse(null);
    }

    protected GuildMember update(GuildMember guildMember) {
        return guildMemberRepo.update(guildMember);
    }

    protected void load(GuildMember[] guildMemberlist)
    {
        Arrays.stream(guildMemberlist).forEach(this::save);
    }

    public List<GuildMember> findByGuildId(UUID guildId) {
        return guildMemberRepo.findByGuildId(guildId);
    }

    public void deleteById(UUID guildId, UUID memberId) {
        guildMemberRepo.deleteById(new GuildMemberCompositeKey(guildId, memberId));
    }
}
