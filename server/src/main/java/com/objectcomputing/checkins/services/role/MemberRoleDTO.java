package com.objectcomputing.checkins.services.role;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Introspected
public class MemberRoleDTO {

    @Schema(description = "The id of the role", required = true)
    private UUID roleId;

    @Schema(description = "The name of the role", required = true)
    private String role;

    @Schema(description = "The description of the role")
    private String description;

    @Schema(description = "List of member ids with the role")
    private List<UUID> memberIds;

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UUID> memberIds) {
        this.memberIds = memberIds;
    }
}
