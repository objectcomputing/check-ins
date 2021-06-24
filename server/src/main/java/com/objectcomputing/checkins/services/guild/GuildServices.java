package com.objectcomputing.checkins.services.guild;

import java.util.Set;
import java.util.UUID;

public interface GuildServices {
    GuildResponseDTO read(UUID id);

    GuildResponseDTO save(GuildCreateDTO g);

    GuildResponseDTO update(GuildUpdateDTO g);

    Set<GuildResponseDTO> findByFields(String name, UUID memberid);

    boolean delete(UUID id);
}
