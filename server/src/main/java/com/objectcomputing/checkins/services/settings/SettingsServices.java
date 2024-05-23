package com.objectcomputing.checkins.services.settings;

import io.micrometer.context.Nullable;

import java.util.List;
import java.util.UUID;

public interface SettingsServices {
    
    Setting save(Setting setting);

    Setting update(Setting setting);

    List<SettingsResponseDTO> findByName(@Nullable String name);

    Boolean delete(UUID id);
}
