package com.objectcomputing.checkins.services.tags;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Introspected
public class TagCreateDTO {

    @NotNull
    @Schema(description = "the name of the tag", required = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
