package com.objectcomputing.checkins.services.role;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;

@Introspected
public class RoleCreateDTO {
//    @NotBlank
//    @Schema(required = true, description = "name of the role")
//    private RoleType role;

    @NotNull
    @Column(name = "role")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "role this member has", required = true,
            allowableValues = {ADMIN_ROLE, PDL_ROLE, MEMBER_ROLE})
    private RoleType role;

    @Nullable
    @Schema(description = "description of the role")
    private String description;

    @Schema(description = "members of this role")
    private List<RoleMemberCreateDTO> roleMembers;

    public RoleCreateDTO(RoleType role, @Nullable String description) {
        this.role = role;
        this.description = description;
    }

    public RoleCreateDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleCreateDTO that = (RoleCreateDTO) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, description);
    }

    public List<RoleMemberCreateDTO> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(List<RoleMemberCreateDTO> roleMembers) {
        this.roleMembers = roleMembers;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Introspected
    public static class RoleMemberCreateDTO {

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this role")
        private UUID memberId;

        public RoleMemberCreateDTO(UUID memberId, Boolean lead) {
            this.memberId = memberId;
            this.lead = lead;
        }

        public Boolean getLead() {
            return lead;
        }

        public void setLead(Boolean lead) {
            this.lead = lead;
        }

        public UUID getMemberId() {
            return memberId;
        }

        public void setMemberId(UUID memberId) {
            this.memberId = memberId;
        }
    }
}
