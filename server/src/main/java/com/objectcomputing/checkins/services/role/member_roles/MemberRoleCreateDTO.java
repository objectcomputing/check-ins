package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class MemberRoleCreateDTO {
    @NotBlank
    private UUID roleid;

    @NotBlank
    private UUID memberid;

    public UUID getRoleid() {
        return roleid;
    }

    public UUID getMemberid() {
        return memberid;
    }
}
