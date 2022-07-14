package com.objectcomputing.checkins.services.settings;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
public class SettingsCreateDTO {

    @NotNull
    @NotBlank
    @Schema(required = true, description = "name of the setting")
    private String name;

    @NotNull
    @Schema(required = true, description = "value of the setting")
    private String value;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
