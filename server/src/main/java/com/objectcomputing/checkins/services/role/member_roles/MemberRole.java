package com.objectcomputing.checkins.services.role.member_roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import io.micronaut.data.annotation.MappedEntity;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.*;
import java.util.UUID;

@MappedEntity
@Table(name = "member_roles")
public class MemberRole {

    @EmbeddedId
    private final MemberRoleId memberRoleId;

    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    private final UUID memberId;

    @Column(name = "roleid")
    @TypeDef(type = DataType.STRING)
    private final UUID roleId;

    public MemberRole(MemberRoleId memberRoleId, UUID memberId, UUID roleId) {
        this.memberRoleId = memberRoleId;
        this.memberId = memberId;
        this.roleId = roleId;
    }

    public MemberRole(UUID memberProfileId, UUID roleId) {
        this(null, memberProfileId, roleId);
    }

    public MemberRoleId getMemberRoleId() {
        return memberRoleId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public UUID getRoleId() {
        return roleId;
    }

}
