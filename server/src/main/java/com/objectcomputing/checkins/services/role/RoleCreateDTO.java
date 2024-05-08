package com.objectcomputing.checkins.services.role;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;


@Introspected
public class RoleCreateDTO {
    @NotNull
    @Column(name = "role")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The name of the role", required = true)
    private String role;

    @Nullable
    @Schema(description = "The description of the role", nullable = true)
    private String description;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
