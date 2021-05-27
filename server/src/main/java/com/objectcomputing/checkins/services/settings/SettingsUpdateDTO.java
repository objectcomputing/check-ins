package com.objectcomputing.checkins.services.settings;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
public class SettingsUpdateDTO{

    @NotNull
    @Schema(required = true, description = "id of the setting")
    private UUID id;

    @NotNull
    @NotBlank
    @Schema(required = true, description = "name of the setting")
    private String name;

    @Nullable
    @Schema(description = "userId of the setting")
    private UUID userId;
    
    @NotNull
    @NotBlank
    @Schema(required = true, description = "value of the setting")
    private String value; 

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
