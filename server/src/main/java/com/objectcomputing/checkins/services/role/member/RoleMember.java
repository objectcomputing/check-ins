package com.objectcomputing.checkins.services.role.member;

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

@Entity
@Table(name = "member_roles")
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
    private UUID roleid;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    public RoleMember(UUID roleid, UUID memberid) {
        this(null, roleid, memberid);
    }

    public RoleMember(UUID id, UUID roleid, UUID memberid) {
        this.id = id;
        this.roleid = roleid;
        this.memberid = memberid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoleid() {
        return memberid;
    }

    public void setRoleid(UUID memberid) {
        this.memberid = memberid;
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
        com.objectcomputing.checkins.services.role.member.RoleMember that = (com.objectcomputing.checkins.services.role.member.RoleMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(roleid, that.roleid) &&
                Objects.equals(memberid, that.memberid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleid, memberid);
    }

    @Override
    public String toString() {
        return "RoleMember{" +
                "id=" + id +
                ", roleid=" + roleid +
                ", memberid=" + memberid +
                '}';
    }
}

