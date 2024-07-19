package com.objectcomputing.checkins.services.settings;

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
    LOGO_URL("The logo url", Category.THEME, Type.FILE, null),
    FROM_NAME("Email From Name", Category.CHECK_INS, Type.STRING, null),
    FROM_ADDRESS("From Address", Category.CHECK_INS, Type.STRING, null),
    DIRECTORY_ID("Google Drive ID", Category.INTEGRATIONS, Type.STRING, null),
    MJ_APIKEY_PUBLIC("MailJet Public API Key", Category.INTEGRATIONS, Type.STRING, null ),
    MJ_APIKEY_PRIVATE("MailJet Private API Key", Category.INTEGRATIONS, Type.STRING, null );



    private final String description;
    private final Category category;
    private final Type type;
    private String value;

    SettingOption(String description, Category category, Type type, String value) {
        this.description = description;
        this.category = category;
        this.type = type;
        this.value = value;
    }

    public static List<SettingOption> getOptions(){
        return Arrays.asList(SettingOption.values());
    }

    public static boolean isValidOption(String name){
        return Stream.of(SettingOption.values())
                .anyMatch(option -> option.name().equalsIgnoreCase(name));
    }

    @JsonCreator
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
