package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

public interface GuildMemberFixture extends RepositoryFixture{
    default GuildMember createDeafultGuildMember(Guild guild, MemberProfileEntity memberProfileEntity) {
        return getGuildMemberRepository().save(new GuildMember(guild.getId(), memberProfileEntity.getId(),false));
    }
}
