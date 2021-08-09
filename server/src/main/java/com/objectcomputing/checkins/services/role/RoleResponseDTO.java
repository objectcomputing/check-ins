package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.services.role.member.RoleMemberResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class RoleResponseDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the role")
    private RoleType role;

    @Nullable
    @Schema(description = "description of the role")
    private String description;

    List<RoleMemberResponseDTO> roleMembers;

    public RoleResponseDTO(UUID id, RoleType role, @Nullable String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public RoleResponseDTO(String id, RoleType role, @Nullable String description) {
        this(UUID.fromString(id), role, description);
    }

    public RoleResponseDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "RoleResponseDTO{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleResponseDTO that = (RoleResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(role, that.role) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, description);
    }

    public List<RoleMemberResponseDTO> getRoleMembers() {
        if (roleMembers == null) {
            roleMembers = new ArrayList<>();
        }
        return roleMembers;
    }

    public void setRoleMembers(List<RoleMemberResponseDTO> roleMembers) {
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
}
