package com.objectcomputing.checkins.services.settings;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class SettingsLoader {

    @Inject
    ApplicationContext applicationContext;

    public static List<Setting> allSettings;

    @EventListener
    public void loadSettingsData(final StartupEvent event) {
        loadSettings();
    }

    @EventListener
    public void loadSettingsData(final SettingsUpdatedEvent event) {
        loadSettings();
    }

    private void loadSettings() {
        SettingsServices settingsServices = new SettingsServicesImpl(applicationContext.getBean(SettingsRepository.class));
        allSettings = settingsServices.findAllSettings();
    }

    public static Setting getSetting(final String name) {
        Setting existingSetting = allSettings.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
        if (existingSetting == null) {
            Setting newSetting = new Setting(name, "");
            allSettings.add(newSetting);
            return newSetting;
        }
        return existingSetting;
    }
}
