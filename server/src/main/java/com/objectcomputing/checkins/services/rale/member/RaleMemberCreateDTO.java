package com.objectcomputing.checkins.services.rale.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class RaleMemberCreateDTO {

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    @NotNull
    @Schema(description = "Rale to which the member belongs")
    private UUID raleId;

    @NotNull
    @Schema(description = "Member who is on this rale")
    private UUID memberId;

    public RaleMemberCreateDTO(UUID raleId, UUID memberId, Boolean lead) {
        this.raleId = raleId;
        this.memberId = memberId;
        this.lead = lead;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }

    public UUID getRaleId() {
        return raleId;
    }

    public void setRaleId(UUID raleId) {
        this.raleId = raleId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }
}
