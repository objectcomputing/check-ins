package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.settings.Setting;
import com.objectcomputing.checkins.services.settings.SettingOption;

public interface SettingsFixture extends RepositoryFixture {

    default Setting createADefaultSetting() {
        return getSettingsRepository().save(new Setting(SettingOption.LOGO_URL.toString(), "url.com"));
    }
}
