package com.objectcomputing.checkins.services.team;

import java.util.Set;
import java.util.UUID;

public interface TeamServices {
    TeamResponseDTO read(UUID id);

    TeamResponseDTO save(TeamCreateDTO g);

    TeamResponseDTO update(TeamUpdateDTO g);

    Set<TeamResponseDTO> findByFields(String name, UUID memberId);

    boolean delete(UUID id);
}
