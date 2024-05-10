package com.objectcomputing.checkins.services.tags;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Introspected
public class TagCreateDTO {

    @NotNull
    @Schema(description = "the name of the tag")
    private String name;

}
