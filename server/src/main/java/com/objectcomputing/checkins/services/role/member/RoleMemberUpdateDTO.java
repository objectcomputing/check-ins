package com.objectcomputing.checkins.services.role.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class RoleMemberUpdateDTO {

    @Schema(description = "ID of the entity to update")
    private UUID id;

    @NotNull
    @Schema(description = "Role to which the member belongs")
    private UUID roleId;

    @NotNull
    @Schema(description = "Member who is on this role")
    private UUID memberId;

    public RoleMemberUpdateDTO(UUID id, UUID roleId, UUID memberId) {
        this.id = id;
        this.roleId = roleId;
        this.memberId = memberId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoleid() {
        return roleId;
    }

    public void setRoleid(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getMemberid() {
        return memberId;
    }

    public void setMemberid(UUID memberId) {
        this.memberId = memberId;
    }
}