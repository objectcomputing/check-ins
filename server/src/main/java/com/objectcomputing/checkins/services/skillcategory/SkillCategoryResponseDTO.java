package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.annotation.Introspected;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Introspected
public class SkillCategoryResponseDTO {
    private UUID id;

    private String name;

    private String description;

    private List<String> skills;

    public static SkillCategoryResponseDTO create(SkillCategory skillCategory, List<Skill> skills) {
        SkillCategoryResponseDTO dto = new SkillCategoryResponseDTO();
        dto.setId(skillCategory.getId());
        dto.setName(skillCategory.getName());
        dto.setDescription(skillCategory.getDescription());
        dto.setSkills(skills.stream().map(Skill::getName).collect(Collectors.toList()));
        return dto;
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillCategoryResponseDTO dto = (SkillCategoryResponseDTO) o;
        return Objects.equals(id, dto.id) && Objects.equals(name, dto.name) && Objects.equals(description, dto.description) && Objects.equals(skills, dto.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, skills);
    }
}
