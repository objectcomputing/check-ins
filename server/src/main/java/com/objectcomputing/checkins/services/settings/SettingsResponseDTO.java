package com.objectcomputing.checkins.services.settings;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Introspected
@AllArgsConstructor
public class SettingsResponseDTO {

    @NotNull
    @Schema(description = "id of the setting")
    private UUID id;

    @NotBlank
    @Schema(description = "name of the setting")
    private String name;

    @NotBlank
    @Schema(description = "description for the setting")
    private String description;

    @NotNull
    @Schema(description = "category of the setting")
    private SettingOption.Category category;

    @NotNull
    @Schema(description = "type of the setting")
    private SettingOption.Type type;

    @NotBlank
    @Schema(description = "value of the setting")
    private String value;

    public SettingsResponseDTO() {
    }
}
