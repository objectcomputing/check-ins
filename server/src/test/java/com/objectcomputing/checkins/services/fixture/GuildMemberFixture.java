package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface GuildMemberFixture extends RepositoryFixture{

    default GuildMember createDefaultGuildMember(Guild guild, MemberProfile memberProfile) {
        return getGuildMemberRepository().save(new GuildMember(guild.getId(), memberProfile.getId(), false));
    }

    default GuildMember createDefaultGuildMemberLead(Guild guild, MemberProfile memberProfile) {
        return getGuildMemberRepository().save(new GuildMember(guild.getId(), memberProfile.getId(), true));
    }
}
