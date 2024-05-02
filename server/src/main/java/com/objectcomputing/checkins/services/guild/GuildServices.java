package com.objectcomputing.checkins.services.guild;

import java.util.Set;
import java.util.UUID;

public interface GuildServices {
    GuildResponseDTO read(UUID id);

    GuildResponseDTO save(GuildCreateDTO guild);

    GuildResponseDTO update(GuildUpdateDTO guild);

    Set<GuildResponseDTO> findByFields(String name, UUID memberId);

    boolean delete(UUID id);
}
