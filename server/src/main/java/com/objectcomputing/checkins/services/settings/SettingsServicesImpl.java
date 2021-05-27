package com.objectcomputing.checkins.services.settings;

import java.util.Set;
import java.util.UUID;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;

public class SettingsServicesImpl implements SettingsServices {

    private final SettingsRepository settingsRepository;

    public SettingsServicesImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Setting save(Setting setting) {
        if (setting.getId() != null) {
            throw new AlreadyExistsException("This setting already exists");
        }
        return settingsRepository.save(setting);
    }

    public Setting update(Setting setting) {
        if (setting.getId() != null && settingsRepository.findById(setting.getId()).isPresent()) {
            return settingsRepository.update(setting);
        } else {
            throw new BadArgException(String.format("Setting %s does not exist, cannot update", setting.getId()));
        }
    }

    public Set<Setting> findByValue(String value, String name, UUID userId) {
        return null;
    }

    public void delete(UUID id) {
    }
}
