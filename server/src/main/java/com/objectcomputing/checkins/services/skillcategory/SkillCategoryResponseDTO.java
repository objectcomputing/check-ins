package com.objectcomputing.checkins.services.skillcategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.annotation.Introspected;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class SkillCategoryResponseDTO {
    private UUID id;

    private String name;

    private String description;
    private List<Skill> skills;

    public static SkillCategoryResponseDTO create(SkillCategory skillCategory, List<Skill> skills) {
        SkillCategoryResponseDTO dto = new SkillCategoryResponseDTO();
        dto.setId(skillCategory.getId());
        dto.setName(skillCategory.getName());
        dto.setDescription(skillCategory.getDescription());
        dto.setSkills(skills);
        return dto;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillCategoryResponseDTO that = (SkillCategoryResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, skills);
    }

    @Override
    public String toString() {
        return "SkillCategoryResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skills=" + skills +
                '}';
    }
}
