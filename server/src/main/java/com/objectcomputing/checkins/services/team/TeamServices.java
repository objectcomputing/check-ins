package com.objectcomputing.checkins.services.team;

import java.util.Set;
import java.util.UUID;

public interface TeamServices {
    Team read(UUID id);

    Team save(Team g);

    Team update(Team g);

    Set<Team> findByFields(String name, UUID memberid);
}
