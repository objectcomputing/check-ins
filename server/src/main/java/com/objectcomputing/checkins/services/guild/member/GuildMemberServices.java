package com.objectcomputing.checkins.services.guild.member;

import java.util.Set;
import java.util.UUID;

public interface GuildMemberServices {

    GuildMember save(GuildMember guildMember, boolean sendEmail);

    GuildMember read(UUID id);

    GuildMember update(GuildMember guildMember);

    void delete(UUID id, boolean sendEmail);

    Set<GuildMember> findByFields(UUID guildid, UUID memberid, Boolean lead);
}
