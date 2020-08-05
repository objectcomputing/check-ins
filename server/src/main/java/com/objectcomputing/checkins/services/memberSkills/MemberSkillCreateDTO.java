package com.objectcomputing.checkins.services.memberSkills;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class MemberSkillCreateDTO {

    @NotBlank
    @Schema(description = "the id of the member profile")
    private UUID memberid;

    @Schema(description = "the id of the skill")
    private UUID skillid;

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    public UUID getSkillid() {
        return skillid;
    }

    public void setSkillid(UUID skillid) {
        this.skillid = skillid;
    }
}
