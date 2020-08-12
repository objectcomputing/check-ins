package com.objectcomputing.checkins.services.memberSkill;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class MemberSkillCreateDTO {

    @NotNull
    @Schema(description = "the id of the member profile", required = true)
    private UUID memberid;

    @NotNull
    @Schema(description = "the id of the skill", required = true)
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
