package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Introspected
public class SkillsReportRequestDTO {
    @NotNull
    @Schema(required = true, description = "A list of requested skills")
    private List<SkillLevelDTO> skills;

    @Nullable
    @Schema(description = "A set of members")
    private Set<UUID> members;

    @Nullable
    @Schema(description = "Each returned member must have all requested skills (or not)")
    private boolean inclusive =  false;

    public List<SkillLevelDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillLevelDTO> skills) {
        this.skills = skills;
    }

    @Nullable
    public Set<UUID> getMembers() {
        return members;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    @Nullable
    public boolean isInclusive() {
        return inclusive;
    }

    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }
}