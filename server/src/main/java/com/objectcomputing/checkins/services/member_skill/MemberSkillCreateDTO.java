package com.objectcomputing.checkins.services.member_skill;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class MemberSkillCreateDTO {

    @NotNull
    @Schema(description = "the id of the member profile", required = true)
    private UUID memberid;

    @NotNull
    @Schema(description = "the id of the skill", required = true)
    private UUID skillid;

    @Schema(description = "the member's expertise level for this skill")
    private String skilllevel;

    @Schema(description = "the last used date of the skill")
    private LocalDate lastuseddate;

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

    public String getSkilllevel() {
        return skilllevel;
    }

    public void setSkilllevel(String skilllevel) {
        this.skilllevel = skilllevel;
    }

    public LocalDate getLastuseddate() {
        return lastuseddate;
    }

    public void setLastuseddate(LocalDate lastuseddate) {
        this.lastuseddate = lastuseddate;
    }
}
