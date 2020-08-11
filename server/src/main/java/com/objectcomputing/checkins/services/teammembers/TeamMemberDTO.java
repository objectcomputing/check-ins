package com.objectcomputing.checkins.services.teammembers;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TeamMemberDTO {

    @NotNull
    private UUID teamId;
    @NotNull
    private UUID memberId;
    private UUID uuid;
    private boolean lead;

    public TeamMemberDTO(UUID uuid, UUID teamId, UUID memberId, boolean lead) {
        this.uuid = uuid;
        this.teamId = teamId;
        this.memberId = memberId;
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

    public boolean isLead() {
        return lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
