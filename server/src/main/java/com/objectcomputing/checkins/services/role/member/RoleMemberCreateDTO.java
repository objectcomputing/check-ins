package com.objectcomputing.checkins.services.role.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class RoleMemberCreateDTO {

    @NotNull
    @Schema(description = "Role to which the member belongs")
    private UUID roleId;

    @NotNull
    @Schema(description = "Member who is on this role")
    private UUID memberId;

    public RoleMemberCreateDTO(UUID roleId, UUID memberId) {
        this.roleId = roleId;
        this.memberId = memberId;
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
