package com.objectcomputing.checkins.services.settings;

import java.util.Set;
import java.util.UUID;

public interface SettingsServices {
    
    Setting save(Setting settings);

    Setting update(Setting setting);

    Set<Setting> findByValue(String value, String name, UUID userId);

    void delete(UUID id);    
}
