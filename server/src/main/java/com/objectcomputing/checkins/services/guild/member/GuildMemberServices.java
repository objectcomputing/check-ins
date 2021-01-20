package com.objectcomputing.checkins.services.guild.member;

import java.util.Set;
import java.util.UUID;

public interface GuildMemberServices {

    GuildMember save(GuildMember guildMember);

    GuildMember read(UUID id);

    GuildMember update(GuildMember guildMember);

    Set<GuildMember> findByFields(UUID guildid, UUID memberid, Boolean lead);

    Boolean delete(UUID id);

}
