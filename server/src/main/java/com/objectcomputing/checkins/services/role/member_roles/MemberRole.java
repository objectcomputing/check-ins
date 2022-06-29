package com.objectcomputing.checkins.services.role.member_roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.*;
import java.util.UUID;

@Introspected
@MappedEntity
@Table(name = "member_roles")
public class MemberRole {

    @EmbeddedId
    private final MemberRoleId memberRoleId;

    public MemberRole(MemberRoleId memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public MemberRole(UUID memberProfileId, UUID roleId) {
        this(new MemberRoleId(memberProfileId, roleId));
    }

    public MemberRoleId getMemberRoleId() {
        return memberRoleId;
    }

}
