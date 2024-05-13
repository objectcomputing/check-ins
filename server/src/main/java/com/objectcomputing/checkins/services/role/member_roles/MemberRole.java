package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;

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
