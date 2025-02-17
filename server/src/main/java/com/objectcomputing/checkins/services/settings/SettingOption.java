package com.objectcomputing.checkins.services.settings;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Introspected
@JsonSerialize(using = SettingOptionSerializer.class)
@JsonDeserialize(using = SettingOptionDeserializer.class)
public enum SettingOption {
    LOGO_URL("The logo url", Category.THEME, Type.FILE),
    PULSE_EMAIL_FREQUENCY("The Pulse Email Frequency", Category.CHECK_INS, Type.STRING, List.of("weekly", "bi-weekly", "monthly"));

    private final String description;
    private final Category category;
    private final Type type;
    private final List<String> values;

    SettingOption(String description, Category category, Type type) {
        this.description = description;
        this.category = category;
        this.type = type;
        this.values = List.of();
    }

    SettingOption(String description, Category category, Type type,
                  List<String> values) {
        this.description = description;
        this.category = category;
        this.type = type;
        this.values = values;
    }

    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public static List<SettingOption> getOptions(){
        return Arrays.asList(SettingOption.values());
    }

    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public static boolean isValidOption(String name){
        return Stream.of(SettingOption.values())
                .anyMatch(option -> option.name().equalsIgnoreCase(name));
    }

    @JsonCreator
    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public static SettingOption fromName(String name) {
        for (SettingOption option : values()) {
            if (option.name().equalsIgnoreCase(name)) {
                return option;
            }
        }
        throw new UnsupportedOperationException(String.format("Unknown permission: '%s'", name));
    }

    public enum Category {
        THEME, INTEGRATIONS, CHECK_INS, REVIEWS
    }

    public enum Type {
        FILE, COLOR, STRING, BOOLEAN, NUMBER
    }
}
