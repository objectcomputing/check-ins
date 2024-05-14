package com.objectcomputing.checkins.services.settings;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Introspected
public class SettingsUpdateDTO{

    @NotNull
    @Schema(description = "id of the setting")
    private UUID id;

    @NotNull
    @NotBlank
    @Schema(description = "name of the setting")
    private String name;
    
    @NotNull
    @NotBlank
    @Schema(description = "value of the setting")
    private String value;

}
