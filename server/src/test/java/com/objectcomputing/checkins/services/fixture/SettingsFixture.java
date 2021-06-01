package com.objectcomputing.checkins.services.fixture;

import java.util.UUID;

import com.objectcomputing.checkins.services.settings.Setting;

public interface SettingsFixture extends RepositoryFixture {

    default Setting createADefaultSetting() {
        return getSettingsRepository().save(new Setting("dark-mode", UUID.randomUUID(), "on"));
    }
}
