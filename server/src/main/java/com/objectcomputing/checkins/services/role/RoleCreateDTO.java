package com.objectcomputing.checkins.services.role;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;

@Introspected
public class RoleCreateDTO {
    @NotNull
    @Column(name = "role")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "role this member has", required = true,
            allowableValues = {ADMIN_ROLE, PDL_ROLE, MEMBER_ROLE})
    private RoleType role;

    @Nullable
    @Schema(description = "description of the check in note", nullable = true)
    private String description;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }
}
