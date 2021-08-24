package com.objectcomputing.checkins.services.role.member_roles;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
//import io.micronaut.data.annotation.Transient;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MemberRoleId {

    @TypeDef(type = DataType.STRING)
    @Column(name = "memberid")
    private final UUID memberId;

    @TypeDef(type = DataType.STRING)
    @Column(name = "roleid")
    private final UUID roleId;

    public MemberRoleId(UUID memberId, UUID roleId) {
        this.memberId = memberId;
        this.roleId = roleId;
    }


    public UUID getMemberId() {
        return memberId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberRoleId that = (MemberRoleId) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, roleId);
    }
}
