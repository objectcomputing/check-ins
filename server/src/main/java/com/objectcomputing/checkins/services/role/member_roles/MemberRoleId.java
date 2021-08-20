package com.objectcomputing.checkins.services.role.member_roles;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MemberRoleId {

    @Column(name = "memberid")
    private final UUID memberProfileId;

    @Column(name = "roleid")
    private final UUID roleId;

    public MemberRoleId(UUID memberProfileId, UUID roleId) {
        this.memberProfileId = memberProfileId;
        this.roleId = roleId;
    }

    public UUID getMemberProfileId() {
        return memberProfileId;
    }

    public UUID getRoleId() {
        return roleId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberRoleId that = (MemberRoleId) o;
        return Objects.equals(memberProfileId, that.memberProfileId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberProfileId, roleId);
    }
}
