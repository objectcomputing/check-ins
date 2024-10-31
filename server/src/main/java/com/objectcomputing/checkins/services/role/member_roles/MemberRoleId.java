package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@Introspected
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
