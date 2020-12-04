package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillResponseDTO;
import com.objectcomputing.checkins.services.skills.tags.SkillSkillTagLookup;
import com.objectcomputing.checkins.services.skills.tags.SkillTag;
import com.objectcomputing.checkins.services.skills.tags.SkillTagCreateDTO;
import com.objectcomputing.checkins.services.skills.tags.SkillTagUpdateDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface SkillFixture extends RepositoryFixture {

    default Skill createADefaultSkill() {
        return getSkillRepository().save(new Skill("Limb regeneration", true,
                "Regenerate a lost limb", false));
    }

    default SkillTag createADefaultSkillTag() {
        return getSkillTagRepository().save(new SkillTag("mocking"));
    }

    default Skill createADefaultTaggedSkill() {
        SkillTag tag = createADefaultSkillTag();
        Skill skill = getSkillRepository().save(new Skill("Limb regeneration", true,
                "Regenerate a lost limb", false));
        skill.setTags(List.of(tag));
        getSkillTagLookupRepository().save(new SkillSkillTagLookup(skill.getId(), tag.getId()));
        return skill;
    }

    default SkillTagCreateDTO createFromEntity(SkillTag entity) {
        SkillTagCreateDTO dto = new SkillTagCreateDTO();
        dto.setName(entity.getName());
        if (entity.getSkills() != null) {
            dto.setSkills(entity.getSkills().stream().map(Skill::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    default SkillTagUpdateDTO updateFromEntity(SkillTag entity) {
        SkillTagUpdateDTO dto = new SkillTagUpdateDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getSkills() != null) {
            dto.setSkills(entity.getSkills().stream().map(skill -> {
                SkillResponseDTO skillDto = new SkillResponseDTO();
                dto.setId(skill.getId());
                dto.setName(skill.getName());rm 
                return skillDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
