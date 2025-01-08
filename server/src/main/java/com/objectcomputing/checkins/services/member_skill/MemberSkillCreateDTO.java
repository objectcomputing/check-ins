package com.objectcomputing.checkins.services.member_skill;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class MemberSkillCreateDTO {

    @NotNull
    @Schema(description = "the id of the member profile")
    private UUID memberid;

    @NotNull
    @Schema(description = "the id of the skill")
    private UUID skillid;

    @Schema(description = "the member's expertise level for this skill")
    private String skilllevel;

    @Schema(description = "the last used date of the skill")
    private LocalDate lastuseddate;

    @Schema(description = "the member interest")
    public boolean interested;
}
