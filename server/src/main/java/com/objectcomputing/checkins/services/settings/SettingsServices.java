package com.objectcomputing.checkins.services.settings;

import java.util.List;
import java.util.UUID;

public interface SettingsServices {
    
    Setting save(Setting settings);

    Setting update(Setting setting);

    List<SettingsResponseDTO> findByName(String name);

    void delete(UUID id);    
}
