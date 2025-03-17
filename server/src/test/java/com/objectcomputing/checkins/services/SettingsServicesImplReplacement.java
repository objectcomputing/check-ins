package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.settings.Setting;
import com.objectcomputing.checkins.services.settings.SettingsServicesImpl;
import com.objectcomputing.checkins.services.settings.SettingsServices;

import java.util.*;

import jakarta.inject.Singleton;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

@Singleton
@Replaces(SettingsServicesImpl.class)
@Requires(property = "replace.settingsservicesimpl", value = StringUtils.TRUE)
public class SettingsServicesImplReplacement implements SettingsServices {
    private HashMap<String, Setting> map = new HashMap<>();

    public SettingsServicesImplReplacement() {
    }

    // *******************************************************************
    // Test Interface
    // *******************************************************************
    public void reset() {
        map.clear();
    }

    @Override
    public Setting save(Setting setting) {
        Setting newSetting = new Setting(UUID.randomUUID(), setting.getName(), setting.getValue());
        map.put(setting.getName(), newSetting);
        return newSetting;
    }

    @Override
    public Setting update(String name, String value) {
        Setting found = map.get(name);
        if(found != null) found.setValue(value);
        else return save(new Setting(name, value));
        return found;
    }

    @Override
    public Setting findByName(String name) {
        Setting found = map.get(name);
        if(found == null) throw new NotFoundException("Setting with name " + name + " not found.");
        return found;
    }

    @Override
    public List<Setting> findAllSettings() {
        List<Setting> settings = Arrays.<Setting>stream((Setting[]) map.values().toArray()).toList();
        return settings;
    }

    @Override
    public boolean delete(UUID id) {
        Optional<Setting> found = map.values().stream().filter((current -> current.getId().equals(id))).findFirst();
        if(found.isPresent()) {
            map.remove(found.get().getName());
            return true;
        }
        return false;
    }
}
