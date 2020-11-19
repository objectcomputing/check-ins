package com.objectcomputing.checkins.services.skills.tags;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface SkillTagService {
    SkillTagResponseDTO save(SkillTagCreateDTO saveMe);
    SkillTagResponseDTO update(SkillTagUpdateDTO updateMe);
    SkillTagResponseDTO findById(@NotNull UUID id);
    List<SkillTagResponseDTO> search(@Nullable String name, @Nullable UUID skillId);
}
