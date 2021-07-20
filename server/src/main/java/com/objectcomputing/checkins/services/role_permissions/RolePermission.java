package com.objectcomputing.checkins.services.role_permissions;

import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;

@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to role entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "role this member has", required = true,
            allowableValues = {ADMIN_ROLE, PDL_ROLE, MEMBER_ROLE})
    @TypeDef(type = DataType.OBJECT)
    private RolePermissionType permission;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    public RolePermission(RolePermissionType permission, UUID memberid) {
        this(null, permission, memberid);
    }

    public RolePermission(UUID id, RolePermissionType permission, UUID memberid) {
        this.id = id;
        this.permission = permission;
        this.memberid = memberid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RolePermissionType getPermission() {
        return permission;
    }

    public void setPermission(RolePermissionType permission) {
        this.permission = permission;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermission that = (RolePermission) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(permission, that.permission) &&
                Objects.equals(memberid, that.memberid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permission, memberid);
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", permission=" + permission +
                ", memberid=" + memberid +
                '}';
    }
}
