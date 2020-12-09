package com.objectcomputing.checkins.services.team.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TeamMemberCreateDTO {

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    @NotNull
    @Schema(description = "Team to which the member belongs")
    private UUID teamid;

    @NotNull
    @Schema(description = "Member who is on this team")
    private UUID memberid;

    public TeamMemberCreateDTO(UUID teamid, UUID memberid, Boolean lead) {
        this.teamid = teamid;
        this.memberid = memberid;
        this.lead = lead;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }

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
}
