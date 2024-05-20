package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Introspected
public class SkillsReportRequestDTO {
    @NotNull
    @Schema(description = "A list of requested skills")
    private List<SkillLevelDTO> skills;

    @Nullable
    @Schema(description = "A set of requested members")
    private Set<UUID> members;

    @Nullable
    @Schema(description = "Each returned member must have all requested skills (or not)")
    private Boolean inclusive;

    @NotNull
    public List<SkillLevelDTO> getSkills() {
        return skills;
    }

    public void setSkills(@NotNull List<SkillLevelDTO> skills) {
        this.skills = skills;
    }

    @Nullable
    public Set<UUID> getMembers() {
        return members;
    }

    public void setMembers(@Nullable Set<UUID> members) {
        this.members = members;
    }

    @Nullable
    public Boolean isInclusive() {
        return inclusive;
    }

    public void setInclusive(@Nullable Boolean inclusive) {
        this.inclusive = inclusive;
    }
}
