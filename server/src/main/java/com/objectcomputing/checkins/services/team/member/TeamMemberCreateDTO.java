package com.objectcomputing.checkins.services.team.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TeamMemberCreateDTO {
    @NotNull
    @Schema(description = "id of the team this entry is associated with", required = true)
    private UUID teamid;

    @NotNull
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public UUID getTeamid() {
        return teamid;
    }

    public void setTeamid(UUID teamid) {
        this.teamid = teamid;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    public Boolean isLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }
}
