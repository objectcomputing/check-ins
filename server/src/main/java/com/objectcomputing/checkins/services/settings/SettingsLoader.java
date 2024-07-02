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
        SettingsServices settingsServices = new SettingsServicesImpl(applicationContext.getBean(SettingsRepository.class));
        allSettings = settingsServices.findAllSettings();
    }

    public static Setting getSetting(final String name) {
        return allSettings.stream().filter(setting -> name.equals(setting.getName())).findFirst().orElse(null);
    }
}
