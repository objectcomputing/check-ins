package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.services.role.member.RoleMemberResponseDTO;
import com.objectcomputing.checkins.services.role.member.RoleMemberUpdateDTO;
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
    private String name;

    @Nullable
    @Schema(description = "description of the role")
    private String description;

    @Schema(description = "members of this role")
    private List<com.objectcomputing.checkins.services.role.RoleUpdateDTO.RoleMemberUpdateDTO> roleMembers;


    public RoleUpdateDTO(UUID id, String name, @Nullable String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public RoleUpdateDTO(String id, String name, String description) {
        this(nullSafeUUIDFromString(id), name, description);
    }

    public RoleUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "RoleUpdateDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.objectcomputing.checkins.services.role.RoleUpdateDTO updateDTO = (com.objectcomputing.checkins.services.role.RoleUpdateDTO) o;
        return Objects.equals(id, updateDTO.id) &&
                Objects.equals(name, updateDTO.name) &&
                Objects.equals(description, updateDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public List<com.objectcomputing.checkins.services.role.RoleUpdateDTO.RoleMemberUpdateDTO> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(List<com.objectcomputing.checkins.services.role.RoleUpdateDTO.RoleMemberUpdateDTO> roleMembers) {
        this.roleMembers = roleMembers;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        @NotNull
        @Schema(description = "Member who is on this role")
        private UUID memberId;

        public RoleMemberUpdateDTO(UUID id, UUID memberId) {
            this.id = id;
            this.memberId = memberId;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }


//        public RoleType getRole() {
//            return role;
//        }
//
//        public void setRole(RoleType role) {
//            this.role = role;
//        }

        public UUID getMemberId() {
            return memberId;
        }

        public void setMemberId(UUID memberid) {
            this.memberId = memberid;
        }
    }
}
