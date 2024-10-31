package com.objectcomputing.checkins.services.skillcategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
