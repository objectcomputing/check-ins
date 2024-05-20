package com.objectcomputing.checkins.services.skills.combineskills;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Introspected
public class CombineSkillsDTO {

    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The name of the new skill")
    private String name;

    @Schema(description = "The description of the new skill")
    private String description;

    @NotNull
    @Schema(description = "A list of skills that are similar and need to be combined")
    private UUID[] skillsToCombine;

    public CombineSkillsDTO(@NotBlank String name, String description, @NotNull UUID[] skillsToCombine) {
        this.name = name;
        this.description = description;
        this.skillsToCombine = skillsToCombine;
    }

}
