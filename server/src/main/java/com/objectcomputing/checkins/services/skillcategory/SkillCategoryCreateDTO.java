package com.objectcomputing.checkins.services.skillcategory;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Introspected
public class SkillCategoryCreateDTO {

    @NotBlank
    @Schema(description = "The name of the skillcategory")
    private String name;

    @Schema(description = "The description of the skillcategory")
    private String description;

}
