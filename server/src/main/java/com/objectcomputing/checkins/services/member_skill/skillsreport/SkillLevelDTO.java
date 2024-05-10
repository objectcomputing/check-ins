package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class SkillLevelDTO {

    @NotNull
    @Schema(description = "UUID of the skill")
    private UUID id;

    @Nullable
    @Schema(description = "Level of the skill")
    private SkillLevel level;

}
