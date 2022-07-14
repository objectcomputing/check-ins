package com.objectcomputing.checkins.services.skills.combineskills;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class CombineSkillsDTO {

    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The name of the new skill", required = true)
    private String name;

    @Schema(description = "The description of the new skill")
    private String description;

    @NotNull
    @Schema(description = "A list of skills that are similar and need to be combined", required = true)
    private UUID[] skillsToCombine;

    public CombineSkillsDTO(@NotBlank String name, String description, @NotNull UUID[] skillsToCombine) {
        this.name = name;
        this.description = description;
        this.skillsToCombine = skillsToCombine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID[] getSkillsToCombine() {
        return skillsToCombine;
    }

    public void setSkillsToCombine(UUID[] skillsToCombine) {
        this.skillsToCombine = skillsToCombine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CombineSkillsDTO that = (CombineSkillsDTO) o;
        return name.equals(that.name) &&
                description.equals(that.description) &&
                Arrays.equals(skillsToCombine, that.skillsToCombine);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, description);
        result = 31 * result + Arrays.hashCode(skillsToCombine);
        return result;
    }

    @Override
    public String toString() {
        return "CombineSkillsDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", skillsToCombine=" + Arrays.toString(skillsToCombine) +
                '}';
    }
}
