package com.objectcomputing.checkins.services.skillcategory;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@Table(name = "skillcategories")
public class SkillCategory {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The id of the skillcategory")
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "The name of the skillcategory")
    private String name;

    @Column(name = "description")
    @Schema(description = "The description of the skillcategory")
    private String description;

    public SkillCategory() {}

    public SkillCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SkillCategory(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillCategory that = (SkillCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SkillCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
