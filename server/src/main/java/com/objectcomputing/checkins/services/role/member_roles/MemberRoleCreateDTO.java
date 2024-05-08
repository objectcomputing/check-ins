package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Introspected
public class MemberRoleCreateDTO {
    @NotBlank
    @Schema(description = "The id of the role", required = true)
    private UUID roleid;

    @NotBlank
    @Schema(description = "The id of the member", required = true)
    private UUID memberid;

    public UUID getRoleid() {
        return roleid;
    }

    public UUID getMemberid() {
        return memberid;
    }
}
