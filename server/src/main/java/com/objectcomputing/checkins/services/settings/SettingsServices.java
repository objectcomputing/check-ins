package com.objectcomputing.checkins.services.settings;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface SettingsServices {
    
    Setting save(Setting setting);

    Setting update(String name, String value);

    Setting findByName(@NotNull String name);

    List<Setting> findAllSettings();

    boolean delete(UUID id);
}
