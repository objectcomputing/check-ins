package com.objectcomputing.checkins.services.settings;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Introspected
public class SettingsCreateDTO {

    @NotBlank
    @Schema(description = "name of the setting")
    private String name;

    @NotBlank
    @Schema(description = "value of the setting")
    private String value;
    
}
