package com.objectcomputing.checkins.services.rale;

import java.util.Set;
import java.util.UUID;

public interface RaleServices {
    RaleResponseDTO read(UUID id);

    RaleResponseDTO save(RaleCreateDTO g);

    RaleResponseDTO update(RaleUpdateDTO g);

    Set<RaleResponseDTO> findByFields(RaleType rale, UUID memberId);

    boolean delete(UUID id);
}
