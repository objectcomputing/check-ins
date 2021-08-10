package com.objectcomputing.checkins.services.role.member;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "role_member")
public class RoleMember {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to role entry", required = true)
    private UUID id;

    @NotNull
    @Column(name = "roleid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the role this entry is associated with", required = true)
    private UUID roleId;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberId;

    public RoleMember(UUID roleId, UUID memberId) {
        this(null, roleId, memberId);
    }

    public RoleMember(UUID id, UUID roleId, UUID memberId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMember that = (RoleMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleId, memberId);
    }

    @Override
    public String toString() {
        return "RoleMember{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", memberId=" + memberId +
                '}';
    }
}