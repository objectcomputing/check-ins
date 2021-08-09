package com.objectcomputing.checkins.services.role;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDFromString;

@Introspected
public class RoleUpdateDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the role")
    private RoleType role;

    @Nullable
    @Schema(description = "description of the role")
    private String description;

    @Schema(description = "members of this role")
    private List<RoleMemberUpdateDTO> roleMembers;

    public RoleUpdateDTO(UUID id, RoleType role, @Nullable String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public RoleUpdateDTO(String id, RoleType role, String description) {
        this(nullSafeUUIDFromString(id), role, description);
    }

    public RoleUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "RoleUpdateDTO{" +
                "id=" + id +
                ", name='" + role + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleUpdateDTO updateDTO = (RoleUpdateDTO) o;
        return Objects.equals(id, updateDTO.id) &&
                Objects.equals(role, updateDTO.role) &&
                Objects.equals(description, updateDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, description);
    }

    public List<RoleMemberUpdateDTO> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(List<RoleMemberUpdateDTO> roleMembers) {
        this.roleMembers = roleMembers;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    public static class RoleMemberUpdateDTO {
        @Schema(description = "ID of the entity to update")
        private UUID id;

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this role")
        private UUID memberId;

        @NotNull
        @Schema(description = "Role to which the member belongs")
        private UUID roleId;

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

        public UUID getMemberId() {
            return memberId;
        }

        public void setMemberId(UUID memberId) {
            this.memberId = memberId;
        }

        public UUID getRoleId() {
            return roleId;
        }

        public void setRoleId(UUID roleId) {
            this.roleId = roleId;
        }
    }
}
