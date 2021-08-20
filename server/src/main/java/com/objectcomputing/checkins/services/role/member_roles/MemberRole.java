package com.objectcomputing.checkins.services.role.member_roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import io.micronaut.data.annotation.MappedEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import java.util.UUID;

@MappedEntity
@Table(name = "member_roles")
public class MemberRole {

//    @JsonIgnore

    @EmbeddedId
    private MemberRoleId memberRoleId;

    @Column(name = "memberid")
    private UUID memberProfileId;

    @Column(name = "roleid")
    private UUID roleId;

    public MemberRole(MemberRoleId memberRoleId, UUID memberProfileId, UUID roleId) {
        this.memberRoleId = memberRoleId;
        this.memberProfileId = memberProfileId;
        this.roleId = roleId;
    }

    public MemberRole(UUID memberProfileId, UUID roleId) {
        this.memberProfileId = memberProfileId;
        this.roleId = roleId;
    }

    public MemberRoleId getMemberRoleId() {
        return memberRoleId;
    }

    public UUID getMemberProfileId() {
        return memberProfileId;
    }

    public UUID getRoleId() {
        return roleId;
    }
}
