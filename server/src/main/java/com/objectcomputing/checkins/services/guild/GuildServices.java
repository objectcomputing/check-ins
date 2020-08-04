package com.objectcomputing.checkins.services.guild;

import java.util.Set;
import java.util.UUID;

public interface GuildServices {
    Guild read(UUID uuid);

    Guild save(Guild g);

    Guild update(Guild g);

    Set<Guild> findByFields(String name, UUID memberid);
}
