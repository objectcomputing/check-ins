package com.objectcomputing.checkins.services.role.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class RoleMemberUpdateDTO {

    @Schema(description = "ID of the entity to update")
    private UUID id;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    @NotNull
    @Schema(description = "Role to which the member belongs")
    private UUID roleId;

    @NotNull
    @Schema(description = "Member who is on this role")
    private UUID memberId;

    public RoleMemberUpdateDTO(UUID id, UUID roleId, UUID memberId, Boolean lead) {
        this.id = id;
        this.roleId = roleId;
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

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }
}
