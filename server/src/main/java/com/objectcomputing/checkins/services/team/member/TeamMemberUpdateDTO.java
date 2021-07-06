package com.objectcomputing.checkins.services.team.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TeamMemberUpdateDTO {

    @Schema(description = "ID of the entity to update")
    private UUID id;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    @NotNull
    @Schema(description = "Team to which the member belongs")
    private UUID teamId;

    @NotNull
    @Schema(description = "Member who is on this team")
    private UUID memberId;

    public TeamMemberUpdateDTO(UUID id, UUID teamId, UUID memberId, Boolean lead) {
        this.id = id;
        this.teamId = teamId;
        this.memberId = memberId;
        this.lead = lead;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }
}
