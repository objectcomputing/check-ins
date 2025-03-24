package com.objectcomputing.checkins.services.settings;

import lombok.Getter;

public class SettingsUpdatedEvent {

    @Getter
    private final Setting setting;

    public SettingsUpdatedEvent(Setting setting){

        this.setting = setting;
    }

}
