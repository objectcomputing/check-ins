package com.objectcomputing.checkins.services.skills.tags;

import com.objectcomputing.checkins.services.skills.SkillNotFoundException;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import com.objectcomputing.checkins.services.skills.SkillResponseDTO;
import com.objectcomputing.checkins.util.Util;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

public class SkillTagServiceImpl implements SkillTagService {

    private final SkillSkillTagLookupRepository skillTagLookupRepository;
    private final SkillRepository skillRepository;
    private final SkillTagRepository skillTagRepository;

    public SkillTagServiceImpl(SkillSkillTagLookupRepository skillTagLookupRepository,
                               SkillRepository skillRepository,
                               SkillTagRepository skillTagRepository) {
        this.skillTagLookupRepository = skillTagLookupRepository;
        this.skillRepository = skillRepository;
        this.skillTagRepository = skillTagRepository;
    }

    @Override
    public SkillTagResponseDTO save(SkillTagCreateDTO saveMe) {
        SkillTag entity = fromCreateDto(saveMe);
        List<UUID> missingSkills = new ArrayList<>();
        for (UUID skillId : saveMe.getSkills()) {
            skillRepository.findById(skillId).or(() -> {
                missingSkills.add(skillId);
                return java.util.Optional.empty();
            });
        }
        if (!missingSkills.isEmpty()) {
            throw new SkillNotFoundException(missingSkills);
        }
        SkillTag savedEntity = skillTagRepository.save(entity);
        for (UUID skillId : saveMe.getSkills()) {
            skillTagLookupRepository.save(new SkillSkillTagLookup(skillId, savedEntity.getId()));
        }
        return fromEntity(savedEntity);
    }

    @Override
    public SkillTagResponseDTO update(SkillTagUpdateDTO updateMe) {
        SkillTag entity = fromUpdateDto(updateMe);
        return fromEntity(skillTagRepository.update(entity));
    }

    @Override
    public SkillTagResponseDTO findByName(@NotNull String name) {
        return skillTagRepository.findByName(name)
                .map(this::fromEntity).orElse(null);
    }

    @Override
    public SkillTagResponseDTO findById(@NotNull UUID id) {
        return skillTagRepository.findById(id)
                .map(this::fromEntity)
                .orElseThrow(SkillTagNotFoundException::new);
    }

    @Override
    public List<SkillTagResponseDTO> search(@Nullable String name, @Nullable UUID skillId) {
        return skillTagRepository.search(name, nullSafeUUIDToString(skillId));
    }

    SkillTag fromCreateDto(SkillTagCreateDTO dto) {
        SkillTag entity = new SkillTag();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private SkillTagResponseDTO fromEntity(SkillTag savedEntity) {
        SkillTagResponseDTO dto = new SkillTagResponseDTO();
        dto.setId(savedEntity.getId());
        dto.setName(savedEntity.getName());
        dto.setDescription(dto.getDescription());
        dto.setSkills(savedEntity.getSkills().stream().map(skill -> {
            SkillResponseDTO skillDTO = new SkillResponseDTO();
            skillDTO.setId(skill.getId());
            skillDTO.setName(skill.getName());
            skillDTO.setDescription(skill.getDescription());
            skillDTO.setPending(skill.isPending());
            skillDTO.setExtraneous(skill.isExtraneous());
            return skillDTO;
        }).collect(Collectors.toList()));
        return dto;
    }

    private SkillTag fromUpdateDto(SkillTagUpdateDTO dto) {
        SkillTag entity = new SkillTag();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
